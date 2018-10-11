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
import java.lang.reflect.Field;
import java.util.function.BiFunction;

/**
 * A criterion that obtains field values via reflection, and then compares it against a target
 * value using a comparison function.
 *
 * @param <M>
 *        The type of model that the criterion applies to.
 */
public final class FunctionBasedReflectionCriterion<M extends Model>
extends AbstractReflectionBasedCriterion<M> {
  private final Object targetValue;
  private final BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Constructor for {@code FunctionBasedReflectionCriterion}.
   *
   * <p>Initializes an exact match criterion that ensures the specified field of the model matches
   * the specified value.
   *
   * @param targetField
   *        The field in the model that will be checked against this criterion.
   * @param targetValue
   *        The value the field must have for this criterion to match.
   * @param comparisonFunction
   *        The function that compares the field value against the target value and returns whether
   *        or not the two are a match.
   */
  public FunctionBasedReflectionCriterion(
                                   final Field targetField, final Object targetValue,
                                   final BiFunction<Object, Object, Boolean> comparisonFunction) {
    super(targetField);

    this.targetValue        = targetValue;
    this.comparisonFunction = comparisonFunction;
  }

  @Override
  protected boolean valueMatches(final Object currentValue, final M model, final Field field) {
    return this.comparisonFunction.apply(currentValue, this.targetValue);
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(Field `%s` against `%s` via `%s`)",
        this.getTargetField().getName(),
        this.targetValue.toString(),
        this.comparisonFunction.getClass().getCanonicalName());

    return value;
  }
}
