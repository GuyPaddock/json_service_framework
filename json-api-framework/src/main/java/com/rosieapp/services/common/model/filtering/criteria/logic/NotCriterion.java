package com.rosieapp.services.common.model.filtering.criteria.logic;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.FilterCriterion;
import java.util.Objects;

/**
 * A filter criterion that models a logical NOT -- it only matches when the criterion it wraps does
 * <em>not</em> match.
 */
public class NotCriterion<M extends Model>
implements FilterCriterion<M> {
  final FilterCriterion<M> wrappedCriterion;

  /**
   * Constructor for {@code NotCriterion}.
   * <p>
   * Initializes a new criterion to negate the provided criterion.
   *
   * @param wrappedCriterion
   *        The criterion to negate.
   */
  public NotCriterion(FilterCriterion<M> wrappedCriterion) {
    Objects.requireNonNull(wrappedCriterion, "wrappedCriteria");

    this.wrappedCriterion = wrappedCriterion;
  }

  @Override
  public boolean matches(M model) {
    return !this.wrappedCriterion.matches(model);
  }
}
