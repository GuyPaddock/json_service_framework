package com.rosieapp.services.common.model.fieldhandling;

import java.util.Optional;

/**
 * Optional, abstract parent class provided for use by field providers in the system.
 * <p>
 * This implementation provides built-in handling for defaulting missing optional fields.
 */
public abstract class AbstractFieldProvider
implements FieldValueProvider {
  @Override
  public <T> T getOptionalField(T fieldValue, T defaultValue) {
    return Optional.ofNullable(fieldValue).orElse(defaultValue);
  }
}
