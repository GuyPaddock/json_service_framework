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
import com.rosieapp.services.common.model.filtering.ModelFilter;
import java.lang.reflect.Field;

/**
 * A criterion that can be used to select a top-level model by matching on values present in one of
 * the models that the top level model references.
 *
 * <p>The matching is done by applying a model filter to the value of a field within the top-level
 * model. This criterion only matches if that model filter returns a match on the sub-model.
 */
public final class ReferencedModelCriterion<M extends Model, S extends Model>
extends AbstractReflectionBasedCriterion<M> {
  private final ModelFilter<S> subModelFilter;

  /**
   * Constructor for {@code ReferencedModelCriterion}.
   *
   * <p>Initializes the new criterion that returns a match when the provided sub-model filter
   * matches against the value obtained from the provided field.
   *
   * @param referenceField
   *        The field within the model that refers to the sub-model.
   * @param subModelFilter
   *        The filter that will be used to determine if the sub-model matches.
   */
  public ReferencedModelCriterion(final Field referenceField, final ModelFilter<S> subModelFilter) {
    super(referenceField);

    this.subModelFilter = subModelFilter;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected boolean valueMatches(final Object currentValue, final M model, final Field field) {
    final S subModel = (S)currentValue;

    return (currentValue != null) && this.subModelFilter.matches(subModel);
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(Field `%s` (`%s` %s))",
        this.getTargetField().getName(),
        this.getTargetField().getType(),
        this.subModelFilter);

    return value;
  }
}
