package com.rosieapp.services.common.model.fieldhandling;

import java.lang.reflect.Field;

/**
 * A pre-processor that passes through copies or clones of field values, ensuring that changes to
 * source values, and changes to the values in one model instance, have no effect on the values in
 * other instances of the same model.
 *
 * <p>This is the default pre-processor for model fields. It ensures that the contents of all maps
 * and collections are shallowly copied to new instances before being used inside a new model. It
 * also takes advantage of the {@link #clone()} method on all field values that support cloning.
 *
 * <p>Not all field values are cloned or copied; scalar values that do not implement
 * {@code Cloneable} are passed through, as-is, without additional processing. Model implementers
 * should take special care when working with such field values, as the same instances could
 * potentially, unintentionally be shared by several different model instances.
 */
public class CloningPreprocessor
implements FieldValuePreprocessor {
  @Override
  public <T> T preprocessField(Field field, T fieldValue) {
    return ObjectCopier.copy(fieldValue);
  }
}
