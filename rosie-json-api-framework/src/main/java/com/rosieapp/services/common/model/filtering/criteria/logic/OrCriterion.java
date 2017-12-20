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
  final List<FilterCriterion<M>> wrappedCriteria;

  /**
   * Constructor for {@code OrCriterion}.
   * <p>
   * Initializes a new criterion that matches if any of the provided criteria match.
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
   * <p>
   * Initializes a new criterion that matches if any of the provided criteria match.
   *
   * @param wrappedCriteria
   *        The criteria to logically OR together.
   */
  public OrCriterion(final Collection<FilterCriterion<M>> wrappedCriteria) {
    Objects.requireNonNull(wrappedCriteria, "wrappedCriteria");

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
