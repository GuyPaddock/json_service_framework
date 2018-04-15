/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.fieldhandling;

import java.lang.reflect.Field;

/**
 * Field pre-processors are responsible for making last-minute changes to values being set by
 * model builders.
 *
 * <p>A field pre-processor is typically associated with a field via the
 * {@link com.rosieapp.services.common.model.annotation.BuilderPopulatedField} annotation on the
 * field.
 *
 * <p>Pre-processors are typically not invoked for fields without a value (i.e. pre-processors
 * aren't invoked when the field value is {@code null}). However, pre-processors are expected to
 * gracefully handle receiving a {@code null} value should this convention change in the future.
 */
public interface FieldValuePreprocessor {
  /**
   * Pre-processes the value of the specified field.
   *
   * @param   field
   *          The model object field being populated.
   * @param   fieldValue
   *          The value being pre-processed. Can be {@code null}.
   *
   * @param   <T>
   *          The type of field value.
   *
   * @return  The result of preprocessing the value.
   */
  <T> T preprocessField(Field field, T fieldValue);
}
