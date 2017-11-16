package com.rosieapp.services.common.model.field;

public class RequiredAttributeException
extends IllegalArgumentException {
  public RequiredAttributeException(String attributeName) {
    super(String.format("`%s` is a required attribute", attributeName));
  }
}
