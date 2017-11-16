package com.rosieapp.services.common.model.field;

import java.util.Optional;

public class ValidatingFieldHandler
extends AbstractFieldHandler {
  @Override
  public <T> T requireField(final T fieldValue, final String fieldName) {
    return Optional.ofNullable(fieldValue)
      .orElseThrow(() -> new RequiredAttributeException(fieldName));
  }
}
