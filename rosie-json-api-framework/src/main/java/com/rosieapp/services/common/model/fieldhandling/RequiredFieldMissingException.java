/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.fieldhandling;

/**
 * Exception thrown by a validating {@link FieldValueProvider} when a required field is missing a
 * value.
 */
public class RequiredFieldMissingException
extends RuntimeException {
  public RequiredFieldMissingException(final String fieldName) {
    super(
      String.format(
        "`%s` is a required field that has not been provided with a value.", fieldName));
  }
}
