/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.fieldhandling;

/**
 * A field dependency handler that addresses missing required field values by merely returning
 * {@code null}, instead of throwing an exception.
 *
 * @see StrictFieldDependencyHandler
 */
public class LaxFieldDependencyHandler
extends AbstractFieldDependencyHandler {
  /**
   * Returns the value to use when populating the specified required field.
   *
   * <p>If the field value is {@code null}, this handler simply supplies {@code null} in place of
   * the missing value.
   *
   * {@inheritDoc}
   *
   * @return  Either the value to use for the field; or {@code null}, if {@code fieldValue} is
   *          {@code null}.
   */
  @Override
  public <F> F handleRequiredField(final F fieldValue, final String fieldName) {
    return this.handleOptionalField(fieldValue, fieldName, null);
  }
}
