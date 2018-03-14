/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.filtering.criteria.logic;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.FilterCriterion;
import com.rosieapp.services.common.model.filtering.criteria.AbstractFilterCriterion;
import java.util.Objects;

/**
 * A filter criterion that models a logical NOT -- it only matches when the criterion it wraps does
 * <em>not</em> match.
 */
public class NotCriterion<M extends Model>
extends AbstractFilterCriterion<M> {
  private final FilterCriterion<M> wrappedCriterion;

  /**
   * Constructor for {@code NotCriterion}.
   *
   * <p>Initializes a new criterion to negate the provided criterion.
   *
   * @param wrappedCriterion
   *        The criterion to negate.
   */
  public NotCriterion(final FilterCriterion<M> wrappedCriterion) {
    super();

    Objects.requireNonNull(wrappedCriterion, "wrappedCriteria cannot be null");

    this.wrappedCriterion = wrappedCriterion;
  }

  @Override
  public boolean matches(final M model) {
    return !this.wrappedCriterion.matches(model);
  }

  @Override
  public String toString() {
    final String value;

    value =
      String.format(
        "(Not %s)",
        this.wrappedCriterion.toString());

    return value;
  }
}
