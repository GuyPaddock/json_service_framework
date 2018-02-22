/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.Model;
import java.util.LinkedList;
import java.util.List;

/**
 * A builder that creates filters that match models using simple, static lists of criteria.
 *
 * @param <M>
 *        The type of model for which a filter is being constructed.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 */
public class CriteriaBasedFilterBuilder<M extends Model, B extends CriteriaBasedFilterBuilder<M, B>>
implements ModelFilterBuilder<M> {
  final List<FilterCriterion<M>> criteria;

  /**
   * Default constructor for {@code CriteriaBasedFilterBuilder}.
   */
  public CriteriaBasedFilterBuilder() {
    this.criteria = new LinkedList<>();
  }

  /**
   * Adds the specified criterion to the criteria that will be evaluated by the next filter built.
   *
   * @param   criterion
   *          The criterion to evaluate.
   *
   * @return  This object, for chaining builder calls.
   */
  @SuppressWarnings("unchecked")
  public B withCriterion(final FilterCriterion<M> criterion) {
    this.criteria.add(criterion);

    return (B)this;
  }

  @Override
  public ModelFilter<M> build() throws UnsupportedOperationException {
    final CriteriaBasedFilter<M>   filter;

    filter = new CriteriaBasedFilter<>(criteria);

    return filter;
  }
}
