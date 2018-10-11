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
import com.rosieapp.services.common.model.construction.ModelBuilder;

/**
 * Common interface for objects that construct filters for locating existing instances of Rosie
 * JSON API service models.
 *
 * <p>Instances of {@code ModelFilterBuilder} are typically obtained through the
 * {@link ModelBuilder#toFilterBuilder()} method on the builder for the model for which a filter
 * is desired. For example:
 * <pre>{@code
 *   ModelFilter<Person> filter =
 *     Person.getBuilder()
 *       .withName("Bob")
 *       .withAge(24)
 *       .toFilterBuilder();
 * }</pre>
 *
 * <p>The resulting filter builder could then be used to find all people named "Bob" who are age 24.
 * As illustrated by the example above, the filter builder starts out inheriting all of the values
 * that were previously set on the model builder.
 *
 * <p>Only fields that have been given a value through the builder interface will be included in the
 * resulting filter. For instance, in contrast to the earlier example, consider the following:
 * <pre>{@code
 *   ModelFilter<Person> filter =
 *     Person.getBuilder()
 *       .withName("Bob")
 *       .toFilterBuilder();
 * }</pre>
 *
 * <p>This would create a filter builder that can builder filters that find all people named
 * "Bob" -- of any age.
 *
 * @param <M>
 *        The type of model for which a filter is being constructed.
 */
public interface ModelFilterBuilder<M extends Model> {
  /**
   * Build a model filter.
   *
   * <p>The resulting filter can be used to identify existing instances of a model that have values
   * matching what has been set on this builder.
   *
   * @return  A filter that is initialized to find models whose values match what has been built up
   *          through this interface.
   *
   * @throws  UnsupportedOperationException
   *          If the builder or model does not support filtering.
   */
  ModelFilter<M> build() throws UnsupportedOperationException;
}
