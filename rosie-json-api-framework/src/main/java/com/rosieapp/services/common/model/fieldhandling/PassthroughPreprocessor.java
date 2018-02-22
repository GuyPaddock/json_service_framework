/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.fieldhandling;

import java.lang.reflect.Field;

/**
 * A pre-processor that passes field values through, exactly as they are, without any processing at
 * all.
 *
 * <p>This pre-processor should only be used for values that are all right to be shared across
 * several all instances of the same model. For example, this can be used for immutable collections
 * or constant values for faster construction performance and better control over which object
 * instance ends up inside each model instance as values pass through the model's builder.
 */
public class PassthroughPreprocessor
implements FieldValuePreprocessor {
  @Override
  public <T> T preprocessField(final Field field, final T fieldValue) {
    return fieldValue;
  }
}
