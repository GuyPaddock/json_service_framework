package com.rosieapp.services.common.model.annotation;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
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
}
