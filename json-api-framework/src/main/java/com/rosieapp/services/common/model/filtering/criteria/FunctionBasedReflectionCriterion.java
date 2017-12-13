package com.rosieapp.services.common.model.filtering.criteria;

import com.rosieapp.services.common.model.Model;
import java.lang.reflect.Field;
import java.util.function.BiFunction;

/**
 * A criterion that obtains field values via reflection, and then compares it against a target
 * value using a comparison function.
 *
 * @param <M>
 *        The type of model that the criterion applies to.
 */
public final class FunctionBasedReflectionCriterion<M extends Model>
extends AbstractReflectionBasedCriterion<M> {
  final Object targetValue;
  final BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Constructor for {@code FunctionBasedReflectionCriterion}.
   * <p>
   * Initializes an exact match criterion that ensures the specified field of the model matches
   * the specified value.
   *
   * @param targetField
   *        The field in the model that will be checked against this criterion.
   * @param targetValue
   *        The value the field must have for this criterion to match.
   */
  public FunctionBasedReflectionCriterion(
                                   final Field targetField, final Object targetValue,
                                   final BiFunction<Object, Object, Boolean> comparisonFunction) {
    super(targetField);

    this.targetValue        = targetValue;
    this.comparisonFunction = comparisonFunction;
  }

  @Override
  protected boolean valueMatches(final Object currentValue, final M model, final Field field) {
    return this.comparisonFunction.apply(currentValue, targetValue);
  }
}