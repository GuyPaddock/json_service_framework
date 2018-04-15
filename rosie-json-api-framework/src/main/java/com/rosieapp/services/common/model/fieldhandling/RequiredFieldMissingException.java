/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.fieldhandling;

/**
 * Exception thrown by a validating {@link FieldDependencyHandler} when a required field is missing
 * a value.
 */
public class RequiredFieldMissingException
extends RuntimeException {
  /**
   * Constructor for {@code RequiredFieldMissingException}.
   *
   * @param fieldName
   *        The name of the field that was required but was missing.
   */
  public RequiredFieldMissingException(final String fieldName) {
    super(
      String.format(
        "`%s` is a required field that has not been provided with a value", fieldName));
  }
}
