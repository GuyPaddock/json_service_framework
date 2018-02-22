package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.criteria.FunctionBasedCriterion;
import com.rosieapp.services.common.model.filtering.criteria.FunctionBasedReflectionCriterion;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An enumeration of the possible ways to match the value of a field against a known target.
 *
 * <p>Each enum value can be used to produce a concrete {@link FilterCriterion} for matching the
 * value of a particular field against a known value via either the
 * {@link #buildFor(Function, Object)} or {@link #buildFor(Field, Object)} methods.
 */
public enum ComparisonType {
  EQUAL_TO              (Objects::equals),
  LESS_THAN             ((first, second) -> compare(first, second, -1)),
  LESS_THAN_OR_EQUAL    ((first, second) -> compare(first, second, -1, 0)),
  GREATER_THAN          ((first, second) -> compare(first, second,  1)),
  GREATER_THAN_OR_EQUAL ((first, second) -> compare(first, second,  1, 0)),
  STARTS_WITH           (ComparisonType::checkStartsWith),
  CONTAINS              (ComparisonType::checkContains),
  ENDS_WITH             (ComparisonType::checkEndsWith);

  final BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Private constructor for {@code ComparisonType}.
   *
   * <p>The provided function will be used by the {@link FilterCriterion} object that is returned by
   * this enum value whenever the criterion needs to compare a field value against a particular
   * value.
   *
   * @param comparisonFunction
   *        The function to invoke during comparisons.
   */
  ComparisonType(final BiFunction<Object, Object, Boolean> comparisonFunction) {
    this.comparisonFunction = comparisonFunction;
  }

  /**
   * Builds a criterion that compares the value of a field returned by the given value provider
   * against the provided target value.
   *
   * <p>The nature of the comparison is dictated by the enum value on which this method is being
   * invoked.
   *
   * @param   targetValue
   *          The value the field must have for the criterion to match.
   *
   * @param   <M>
   *          The type of model that the criterion applies to.
   *
   * @return  The new filter criterion.
   */
  public <M extends Model> FilterCriterion<M> buildFor(final Function<M, Object> valueProvider,
                                                       final Object targetValue) {
    return new FunctionBasedCriterion<>(valueProvider, targetValue, this.comparisonFunction);
  }

  /**
   * Builds a criterion that compares the value of the specified field against the provided target
   * value.
   *
   * <p>The nature of the comparison is dictated by the enum value on which this method is being
   * invoked.
   *
   * @param   targetField
   *          The field in the model the criterion will be checking against.
   * @param   targetValue
   *          The value the field must have for the criterion to match.
   *
   * @param   <M>
   *          The type of model that the criterion applies to.
   *
   * @return  The new filter criterion.
   */
  public <M extends Model> FilterCriterion<M> buildFor(final Field targetField,
                                                       final Object targetValue) {
    return new FunctionBasedReflectionCriterion<>(
      targetField, targetValue, this.comparisonFunction);
  }

  /**
   * Compares two objects via their {@code compare()} method, and checks the result against the
   * provided magnitude signs.
   *
   * <p>The two objects are considered a match if the signum of comparing the first against the
   * second matches any of the provided signums. For example, if the result returned by
   * {@link Comparable#compareTo(Object)} when comparing {@code first} and {@code second} was
   * {@code -6}, and the {@code targetSignums} included {@code -1}, then the objects are a match
   * because {@code signum(-6) == -1}.
   *
   * @param   first
   *          The first value to compare.
   * @param   second
   *          The second value to compare.
   * @param   targetSignums
   *          A varargs array of the magnitude signs (i.e. results from calling {@code signum()})
   *          that are considered a match.
   *
   * @param   <T>
   *          The type of objects being compared.
   *
   * @return  {@code true} if the result of comparing the objects matched at least one of the
   *          provided {@code targetSignums}; or, {@code false} if it did not.
   *
   * @throws  ClassCastException
   *          If either object does not implement the {@code Comparable} interface; or, the two
   *          objects are not directly comparable (i.e. they implement the interface for different
   *          types that are not assignable to one another).
   */
  protected static <T extends Comparable<T>> boolean compare(final Object first,
                                                             final Object second,
                                                             int... targetSignums) {
    boolean   matches          = false;
    final T   comparableFirst  = toComparable(first),
              comparableSecond = toComparable(second);
    final int compareResult    = comparableFirst.compareTo(comparableSecond);

    for (int targetSigNum : targetSignums) {
      if (Integer.signum(compareResult) == Integer.signum(targetSigNum)) {
        matches = true;
        break;
      }
    }

    return matches;
  }

  /**
   * Checks if the string representation of a second object is contained within the string
   * representation of the first object.
   *
   * @param   haystack
   *          The object which will be checked to see if it contains {@code needle}.
   * @param   needle
   *          The value that is being searched for inside the string representation of
   *          {@code haystack}.
   *
   * @return  {@code true} if the string representation of {@code needle} is found in
   *          {@code haystack}; or, {@code false}, otherwise.
   */
  private static boolean checkContains(Object haystack, Object needle) {
    return Objects.toString(haystack).contains(Objects.toString(needle));
  }

  /**
   * Checks if the string representation of a second object appears as the first few characters
   * of the string representation of the first object.
   *
   * @param   haystack
   *          The object which will be checked to see if it starts with {@code needle}.
   * @param   needle
   *          The value that is being searched for at the start of the string representation of
   *          {@code haystack}.
   *
   * @return  {@code true} if the string representation of {@code needle} is found at the start of
   *          {@code haystack}; or, {@code false}, otherwise.
   */
  private static Boolean checkStartsWith(Object haystack, Object needle) {
    return Objects.toString(haystack).startsWith(Objects.toString(needle));
  }

  /**
   * Checks if the string representation of a second object appears as the last few characters
   * of the string representation of the first object.
   *
   * @param   haystack
   *          The object which will be checked to see if it ends with {@code needle}.
   * @param   needle
   *          The value that is being searched for at the end of the string representation of
   *          {@code haystack}.
   *
   * @return  {@code true} if the string representation of {@code needle} is found at the end of
   *          {@code haystack}; or, {@code false}, otherwise.
   */
  private static Boolean checkEndsWith(Object haystack, Object needle) {
    return Objects.toString(haystack).endsWith(Objects.toString(needle));
  }

  /**
   * Uses voodoo and black magic to coerce an object into being an instance of {@code Comparable}.
   *
   * <p>The object must already extend from a class that implements the {@link Comparable}
   * interface. All this method really does is ensure that it's safe to typecast the object for
   * comparison, throwing an appropriate, helpful error message if it is not safe.
   *
   * @param   object
   *          The object to typecast.
   *
   * @param   <T>
   *          The type of objects being compared.
   *
   * @return  A version of the object that has been safely typecast to support comparison.
   *
   * @throws  ClassCastException
   *          If the object does not implement the {@code Comparable} interface.
   */
  @SuppressWarnings("unchecked")
  protected static <T extends Comparable<T>> T toComparable(final Object object) {
    if (!(object instanceof Comparable)) {
      throw new ClassCastException(
        MessageFormat.format(
          "Provided object is of type {0}, which does not implement Comparable. It must inherit " +
          "from Comparable to support approximate matching.",
          object.getClass().getCanonicalName()));
    }

    return (T)object;
  }
}
