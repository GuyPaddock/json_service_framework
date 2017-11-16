package com.rosieapp.services.common.model.field;

/**
 * Exception thrown by a validating {@link FieldValueHandler} when a required field is missing a
 * value.
 */
public class RequiredFieldMissingException
extends IllegalStateException {
  public RequiredFieldMissingException(String fieldName) {
    super(
      String.format(
        "`%s` is a required field that has not been provided with a value.", fieldName));
  }
}
