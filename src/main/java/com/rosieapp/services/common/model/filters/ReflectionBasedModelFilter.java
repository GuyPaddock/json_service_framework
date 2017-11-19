package com.rosieapp.services.common.model.filters;

import com.rosieapp.services.common.model.Model;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A type of model filter that leverages access to fields via reflection to identify models that
 * match criteria.
 *
 * @param <M> {@inheritDoc}
 */
public class ReflectionBasedModelFilter<M extends Model>
implements ModelFilter<M> {
  private final Map<Field, Object> criteria;

  public ReflectionBasedModelFilter() {
    this.criteria = new HashMap<>();
  }

  /**
   * Adds a criterion to this filter that requires the specified field to equal the provided value.
   *
   * @param field
   *        The field that will be checked.
   *
   * @param targetValue
   *        The value that the field must contain for this filter to match a model.
   */
  public void addCriterion(Field field, Object targetValue) {
    this.criteria.put(field, targetValue);
  }

  @Override
  public boolean matches(final M model) {
    final boolean filterMatches;

    filterMatches =
      this.criteria.entrySet()
        .stream()
        .allMatch((entry) -> this.fieldMatches(model, entry.getKey(), entry.getValue()));

    return filterMatches;
  }

  /**
   * Checks if the specified field in the provided model is equal to the provided value.
   *
   * @param   model
   *          The model being inspected.
   * @param   field
   *          The field being inspected in the model.
   * @param   targetValue
   *          The value that the field must contain in order for the model to match the filter.
   *
   * @return  {@code true} if the specified {@code field} of {@code model} equals
   *          {@code targetValue}; or, {@code false} if it does not.
   */
  private boolean fieldMatches(final M model, final Field field, final Object targetValue) {
    field.setAccessible(true);

    try {
      return Objects.equals(field.get(model), targetValue);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(
        String.format(
          "Unexpectedly failed to access field `%s` on model of type `%s`: %s",
          field.getName(),
          model.getClass().getName(),
          ex.getMessage()),
        ex);
    }
  }
}
