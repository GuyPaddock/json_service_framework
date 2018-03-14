/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
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
 */
public interface FieldValuePreprocessor {
  /**
   * Pre-processes the value of the specified field.
   *
   * @param   field
   *          The model object field being populated.
   * @param   fieldValue
   *          The value being pre-processed.
   *
   * @param   <T>
   *          The type of field value.
   *
   * @return  The result of preprocessing the value.
   */
  <T> T preprocessField(Field field, T fieldValue);
}
