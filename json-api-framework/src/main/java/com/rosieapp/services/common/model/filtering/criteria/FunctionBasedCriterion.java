package com.rosieapp.services.common.model.filtering.criteria;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.FilterCriterion;
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
implements FilterCriterion<M> {
  final Function<M, Object> valueProvider;
  final Object targetValue;
  final BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Constructor for {@code FunctionBasedCriterion}.
   * <p>
   * Initializes a criterion that obtains a value from the model through the provided value
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
    this.valueProvider      = valueProvider;
    this.targetValue        = targetValue;
    this.comparisonFunction = comparisonFunction;
  }

  /**
   * {@inheritDoc}
   * <ul>
   *   <li>The value is obtained using the <em>value provider</em> that was provided when this
   *   criterion was created.</li>
   *   <li>The comparison is done via the <em>comparison function</em> that was provided when this
   *   criterion was created.</li>
   * </ul>
   */
  @Override
  public boolean matches(M model) {
    return comparisonFunction.apply(
      this.valueProvider.apply(model),
      this.targetValue);
  }
}
