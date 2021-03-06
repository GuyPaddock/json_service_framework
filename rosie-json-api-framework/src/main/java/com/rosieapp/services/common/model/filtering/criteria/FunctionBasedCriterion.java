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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A criterion that obtains values from functions and then invokes a separate comparison function
 * to perform its tasks.
 *
 * @param <M>
 *        The type of model that the criterion applies to.
 */
public final class FunctionBasedCriterion<M extends Model>
extends AbstractFilterCriterion<M> {
  private final Function<M, Object> valueProvider;
  private final Object targetValue;
  private final BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Constructor for {@code FunctionBasedCriterion}.
   *
   * <p>Initializes a criterion that obtains a value from the model through the provided value
   * provider function, and then compares the result against a target value using the provided
   * comparision function.
   *
   * @param valueProvider
   *        The supplier that will be called to obtain the value for comparison.
   * @param targetValue
   *        The value the against which the output from the value provider will be compared.
   * @param comparisonFunction
   *        The function used to compare the values.
   */
  public FunctionBasedCriterion(final Function<M, Object> valueProvider,
                                final Object targetValue,
                                final BiFunction<Object, Object, Boolean> comparisonFunction) {
    super();

    this.valueProvider      = valueProvider;
    this.targetValue        = targetValue;
    this.comparisonFunction = comparisonFunction;
  }

  /**
   * {@inheritDoc}
   *
   * <ul>
   *   <li>The value is obtained using the <em>value provider</em> that was provided when this
   *   criterion was created.</li>
   *   <li>The comparison is done via the <em>comparison function</em> that was provided when this
   *   criterion was created.</li>
   * </ul>
   */
  @Override
  public boolean matches(final M model) {
    return this.comparisonFunction.apply(
      this.valueProvider.apply(model),
      this.targetValue);
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(Supplier `%s` against `%s` via `%s`)",
        this.valueProvider.getClass().getCanonicalName(),
        this.targetValue.toString(),
        this.comparisonFunction.getClass().getCanonicalName());

    return value;
  }
}
