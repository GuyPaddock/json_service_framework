package com.rosieapp.services.common.model.field;

public class RequiredFieldMissingException
extends IllegalStateException {
  public RequiredFieldMissingException(String fieldName) {
    super(
      String.format(
        "`%s` is a required field that has not been provided with a value.", fieldName));
  }
}
