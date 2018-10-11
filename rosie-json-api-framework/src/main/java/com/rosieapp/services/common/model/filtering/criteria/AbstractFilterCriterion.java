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
