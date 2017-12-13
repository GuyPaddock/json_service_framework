package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.criteria.logic.AndCriterion;
import java.util.Collection;

/**
 * A basic model filter that simply matches models by evaluating a static list of criteria.
 */
public class CriteriaBasedFilter<M extends Model>
extends AndCriterion<M>
implements ModelFilter<M> {
  /**
   * Constructor for {@code CriteriaBasedFilter}.
   */
  public CriteriaBasedFilter(final Collection<FilterCriterion<M>> criteria) {
    super(criteria);
  }
}
