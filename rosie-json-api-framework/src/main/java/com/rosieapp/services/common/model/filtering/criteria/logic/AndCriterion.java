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
 * A filter criterion that models a logical AND -- it matches if all of the criteria it wraps match.
 */
public class AndCriterion<M extends Model>
extends AbstractFilterCriterion<M> {
  final List<FilterCriterion<M>> wrappedCriteria;

  /**
   * Constructor for {@code OrCriterion}.
   *
   * <p>Initializes a new criterion that matches if all of the provided criteria match.
   *
   * @param wrappedCriteria
   *        The criteria to logically AND together.
   */
  @SafeVarargs
  public AndCriterion(final FilterCriterion<M>... wrappedCriteria) {
    this(Arrays.asList(wrappedCriteria));
  }

  /**
   * Constructor for {@code OrCriterion}.
   *
   * <p>Initializes a new criterion that matches if all of the provided criteria match.
   *
   * @param wrappedCriteria
   *        The criteria to logically AND together.
   */
  public AndCriterion(final Collection<FilterCriterion<M>> wrappedCriteria) {
    super();

    Objects.requireNonNull(wrappedCriteria, "wrappedCriteria");

    this.wrappedCriteria = new LinkedList<>(wrappedCriteria);
  }

  @Override
  public boolean matches(final M model) {
    return this.wrappedCriteria.stream().allMatch((criterion) -> criterion.matches(model));
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(And %s)",
        this.wrappedCriteria.toString());

    return value;
  }
}
