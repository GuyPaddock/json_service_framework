/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.fieldhandling;

import java.util.Optional;

/**
 * A field dependency handler that handles missing required field values by throwing an
 * exception.
 *
 * @see LaxFieldDependencyHandler
 */
public class StrictFieldDependencyHandler
extends AbstractFieldDependencyHandler {
  /**
   * Validates and returns the value to use when populating the specified required field.
   *
   * <p>If the field value is {@code null}, the handler will communicate this by raising a
   * {@link RequiredFieldMissingException}.
   *
   * {@inheritDoc}
   *
   * @return  The non-null value to use for the field.
   *
   * @throws  RequiredFieldMissingException
   *          If the required field value is {@code null}.
   */
  @Override
  public <F> F handleRequiredField(final F fieldValue, final String fieldName)
  throws RequiredFieldMissingException {
    return Optional.ofNullable(fieldValue)
      .orElseThrow(
        () -> new RequiredFieldMissingException(fieldName));
  }
}
