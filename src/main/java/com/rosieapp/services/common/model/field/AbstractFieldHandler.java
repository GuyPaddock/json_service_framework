package com.rosieapp.services.common.model.field;

import java.util.Optional;

/**
 * Optional, abstract parent class provided for use by field handlers in the system.
 * <p>
 * This implementation provides built-in handling for defaulting missing optional fields.
 */
public abstract class AbstractFieldHandler
implements FieldValueHandler {
  @Override
  public <T> T getOptionalField(T fieldValue, T defaultValue) {
    return Optional.ofNullable(fieldValue).orElse(defaultValue);
  }
}
