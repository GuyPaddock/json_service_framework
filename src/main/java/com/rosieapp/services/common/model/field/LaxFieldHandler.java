package com.rosieapp.services.common.model.field;

public class LaxFieldHandler
extends AbstractFieldHandler {
  @Override
  public <T> T requireField(final T fieldValue, final String fieldName) {
    return getFieldOrDefault(fieldValue, null);
  }
}
