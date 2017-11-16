package com.rosieapp.services.common.model.field;

import java.util.Optional;

public abstract class AbstractFieldHandler
implements FieldHandler {
  @Override
  public <T> T getFieldOrDefault(T fieldValue, T defaultValue) {
    return Optional.ofNullable(fieldValue).orElse(defaultValue);
  }
}
