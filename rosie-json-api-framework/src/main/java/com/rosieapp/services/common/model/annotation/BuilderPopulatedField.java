/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.annotation;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
import com.rosieapp.services.common.model.fieldhandling.CloningPreprocessor;
import com.rosieapp.services.common.model.fieldhandling.FieldValuePreprocessor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation on {@link Model} fields that are to be populated via an
 * {@link AnnotationBasedModelBuilder}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BuilderPopulatedField {
  /**
   * Indicates whether or not this field is required to have a value.
   *
   * @return  {@code true} if this field must have a value; or, {@code false} if the value of the
   *          field is optional.
   */
  boolean required() default false;

  /**
   * Controls what processor is invoked to convert a raw value from the model's builder into the
   * value that ends up in a new model instance.
   *
   * <p>The default value ensures that, whenever possible, each model instance ends up with a
   * distinct instance of a field value than other instances of the same model.
   *
   * <p>The pre-processor is not invoked when building a filter. It is only applicable to new model
   * instances.
   *
   * @see com.rosieapp.services.common.model.fieldhandling.CloningPreprocessor
   * @see com.rosieapp.services.common.model.fieldhandling.PassthroughPreprocessor
   *
   * @return  The type of pre-processor to use for this field.
   */
  Class<? extends FieldValuePreprocessor> preprocessor() default CloningPreprocessor.class;
}
