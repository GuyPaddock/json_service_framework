/*
 * Copyright (c) 2017-2018 Rosie Applications Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  private final List<FilterCriterion<M>> criteria;

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
    final CriteriaBasedFilter<M> filter;

    filter = new CriteriaBasedFilter<>(this.criteria);

    return filter;
  }
}
