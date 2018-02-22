package com.rosieapp.services.common.model.filtering.criteria;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.FilterCriterion;

/**
 * Optional abstract parent class for a single filter criterion.
 *
 * @param <M>
 *        The type of model that the criterion applies to.
 */
public abstract class AbstractFilterCriterion<M extends Model>
implements FilterCriterion<M> {
  /**
   * Get a representation of this criterion as a string.
   *
   * <p>All criteria must implement this method.
   *
   * @return The string representation of this criterion.
   */
  @Override
  public abstract String toString();
}
