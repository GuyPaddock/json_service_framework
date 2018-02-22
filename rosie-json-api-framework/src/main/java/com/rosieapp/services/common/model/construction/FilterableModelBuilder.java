/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.construction;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.fieldhandling.FieldValueProvider;
import com.rosieapp.services.common.model.filtering.ReflectionBasedFilterBuilder;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * A convenient parent class for builders that provider their own custom type of model filter
 * builder.
 *
 * <p>The generic types on this class ensure that model builder implementations are prompted to
 * return the correct type of model filter builder.
 *
 * @param <M>
 *        The type of model that the builder builds.
 * @param <FB>
 *        The type of filter builder that this model builder can convert its state into.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 */
public abstract class FilterableModelBuilder<M extends Model,
                                             FB extends ReflectionBasedFilterBuilder<M, FB>,
                                             B extends FilterableModelBuilder<M, FB, B>>
extends AnnotationBasedModelBuilder<M, B> {
  /**
   * Default constructor for {@link FilterableModelBuilder}.
   *
   * <p>Initializes the model builder to strictly validate required fields.
   */
  public FilterableModelBuilder() {
    super();
  }

  /**
   * Constructor for {@link FilterableModelBuilder}.
   *
   * @param   valueProvider
   *          A provider for controlling how optional and required fields are handled during object
   *          construction.
   */
  public FilterableModelBuilder(final FieldValueProvider valueProvider) {
    super(valueProvider);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The returned type of builder is guaranteed to be the specific builder type {@code <FB>} that
   * was specified at the time this builder was declared.
   */
  @Override
  @SuppressWarnings("unchecked")
  public FB toFilterBuilder() {
    return (FB)super.toFilterBuilder();
  }

  @Override
  protected abstract FB createFilterBuilder(final Map<String, Field> targetFields);
}
