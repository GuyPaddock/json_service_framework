/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

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
   *
   * <p>Initializes a new filter that will match models using the provided collection of criteria.
   *
   * @param criteria
   *        The criteria that must all match against a given model in order for that model to be
   *        selected by the new filter.
   */
  public CriteriaBasedFilter(final Collection<FilterCriterion<M>> criteria) {
    super(criteria);
  }
}
