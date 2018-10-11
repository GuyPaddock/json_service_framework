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

/**
 * Provides an interface for checking if a particular criterion matches a given model.
 *
 * <p>At present, this interface and the {@link ModelFilter} interface are identical, but
 * conceptually criteria and model filters are distinct. Allowing one to extend the other also has
 * the added benefit of allowing filters to be composed as criteria within other filters.
 *
 * @param <M>
 *        The type of model that the criterion applies to.
 */
public interface FilterCriterion<M extends Model> {
  /**
   * Indicates whether or not this criterion matches against the provided model.
   *
   * @param   model
   *          The model being checked against the filter.
   *
   * @return  {@code true} if the criterion applies to the specified model; {@code false} if it
   *          doesn't match the model.
   */
  boolean matches(M model);
}
