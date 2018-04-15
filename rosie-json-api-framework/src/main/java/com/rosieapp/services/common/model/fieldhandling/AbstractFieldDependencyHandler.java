/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.fieldhandling;

import java.util.Optional;

/**
 * Optional, abstract parent class provided for use by field dependency handlers in the system.
 *
 * <p>This implementation provides built-in handling for substituting in default values for missing
 * optional fields.
 */
public abstract class AbstractFieldDependencyHandler
implements FieldDependencyHandler {
  @Override
  public <T> T handleOptionalField(final T fieldValue, final String fieldName,
                                   final T defaultValue) {
    return Optional.ofNullable(fieldValue).orElse(defaultValue);
  }
}
