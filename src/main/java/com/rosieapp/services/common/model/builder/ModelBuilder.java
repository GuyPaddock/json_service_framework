package com.rosieapp.services.common.model.builder;

/**
 * Common interface for objects that construct instances of Rosie JSON API service models.
 * <p>
 * Builders are strongly preferred to the traditional object constructors that would traditionally
 * be used on model objects. Builders are significantly more versatile than traditional
 * constructors, providing a natural, fluent interface for assembling immutable, thread-safe model
 * instances.
 * <p>
 * Builders may also perform validation of field values at time of construction, to provide a
 * general assurance that model instances are only constructed if they have valid data. The actual
 * validation performed by the builder is dependent upon its implementation.
 *
 * @param <M>
 *        The type of model that the builder builds.
 */
public interface ModelBuilder<M> {
  /**
   * Builds a model instance using information that has been built up through this interface.
   * <p>
   * Depending upon the underlying implementation, the data being used to populate the model may or
   * not be validated before the model is constructed.
   *
   * @return  The newly-constructed model instance.
   *
   * @throws  IllegalStateException
   *          If any of the information that has been provided to the builder prior to calling this
   *          method is insufficient or invalid for constructing the model.
   */
  M build() throws IllegalStateException;

  /**
   * Builds a shallowly-populated model that consists only of its ID.
   * <p>
   * This is only to be used for cases in which the model represents data that has already been
   * persisted in a remote system. Consequently, the builder must be provided with the ID to use for
   * the new model instance prior to this call.
   * <p>
   * Not all models / builders are required to support this operation. Builders that do not support
   * building a shallow model will throw an {@link UnsupportedOperationException}.
   *
   * @return  The newly-constructed model instance.
   *
   * @throws  IllegalStateException
   *          If the builder was not provided with the ID prior to the call.
   *
   * @throws  UnsupportedOperationException
   *          If the builder or model does not support being constructed in a shallow manner.
   */
  M buildShallow() throws IllegalStateException, UnsupportedOperationException;
}
