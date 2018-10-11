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

package com.rosieapp.services.common.model.filtering.criteria.logic;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.FilterCriterion;
import com.rosieapp.services.common.model.filtering.criteria.AbstractFilterCriterion;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A filter criterion that models a logical OR -- it matches if at least one of the criteria it
 * wraps matches.
 */
public class OrCriterion<M extends Model>
extends AbstractFilterCriterion<M> {
  private final List<FilterCriterion<M>> wrappedCriteria;

  /**
   * Constructor for {@code OrCriterion}.
   *
   * <p>Initializes a new criterion that matches if any of the provided criteria match.
   *
   * @param wrappedCriteria
   *        The criteria to logically OR together.
   */
  @SafeVarargs
  public OrCriterion(final FilterCriterion<M>... wrappedCriteria) {
    this(Arrays.asList(wrappedCriteria));
  }

  /**
   * Constructor for {@code OrCriterion}.
   *
   * <p>Initializes a new criterion that matches if any of the provided criteria match.
   *
   * @param wrappedCriteria
   *        The criteria to logically OR together.
   */
  public OrCriterion(final Collection<FilterCriterion<M>> wrappedCriteria) {
    super();

    Objects.requireNonNull(wrappedCriteria, "wrappedCriteria cannot be null");

    this.wrappedCriteria = new LinkedList<>(wrappedCriteria);
  }

  @Override
  public boolean matches(final M model) {
    return this.wrappedCriteria.stream().anyMatch((criterion) -> criterion.matches(model));
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(Or %s)",
        this.wrappedCriteria.toString());

    return value;
  }
}
