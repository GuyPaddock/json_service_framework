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
