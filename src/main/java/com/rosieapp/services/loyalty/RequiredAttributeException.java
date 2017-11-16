package com.rosieapp.services.loyalty;

public class RequiredAttributeException
extends IllegalArgumentException {
  public RequiredAttributeException(String attributeName) {
    super(String.format("`%s` is a required attribute", attributeName));
  }
}
