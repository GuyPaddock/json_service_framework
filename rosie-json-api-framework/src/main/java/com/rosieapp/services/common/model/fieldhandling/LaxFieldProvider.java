package com.rosieapp.services.common.model.fieldhandling;

/**
 * A field provider that handles missing required field values by merely returning {@code null},
 * instead of throwing an exception.
 */
public class LaxFieldProvider
extends AbstractFieldProvider {
  /**
   * Returns the value to use when populating the specified required field.
   *
   * <p>If the field value is {@code null}, this provider simply supplies {@code null} in place of
   * the missing value.
   *
   * {@inheritDoc}
   *
   * @return  Either the value to use for the field, or {@code null} if {@code fieldValue} is
   *          {@code null}.
   */
  @Override
  public <F> F getRequiredField(final F fieldValue, final String fieldName) {
    return this.getOptionalField(fieldValue, null);
  }
}
