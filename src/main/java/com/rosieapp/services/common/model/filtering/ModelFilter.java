package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.construction.ModelBuilder;

/**
 * Provides an interface for identifying if a given model matches a previously-established set of
 * criteria.
 * <p>
 * Model filters are obtained through the same {@link ModelBuilder} interface used for constructing
 * model instances. The same values used for assembling models are used to construct filters that
 * match them.
 *
 * @param <M>
 *        The type of model that the filter applies to.
 */
public interface ModelFilter<M> {
  /**
   * Indicates whether or not the criteria in this filter matches the provided model.
   *
   * @param   model
   *          The model being checked against the filter.
   *
   * @return  {@code true} if the criteria apply to the specified model; {@code false} if one or
   *          more criteria didn't match the model.
   */
  boolean matches(M model);
}
