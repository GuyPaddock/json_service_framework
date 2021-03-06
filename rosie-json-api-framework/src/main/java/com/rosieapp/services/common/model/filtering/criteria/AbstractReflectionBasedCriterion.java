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

/**
 * A type of model filter that leverages access to fields via reflection to identify models that
 * match criteria.
 *
 * @param <M> {@inheritDoc}
 */
public abstract class AbstractReflectionBasedCriterion<M extends Model>
extends AbstractFilterCriterion<M> {
  private final Field targetField;

  /**
   * Constructor for {@code AbstractReflectionBasedCriterion}.
   *
   * <p>Initializes a criterion that matches against the specified field of the model.
   *
   * @param targetField
   *        The field in the model that will be checked against this criterion.
   */
  public AbstractReflectionBasedCriterion(final Field targetField) {
    super();

    this.targetField = targetField;
  }

  @Override
  public boolean matches(final M model) {
    final Field field = this.targetField;

    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    try {
      return valueMatches(field.get(model), model, field);
    } catch (final IllegalAccessException ex) {
      throw new UnsupportedOperationException(
        String.format(
          "Unexpectedly failed to access field `%s` on model of type `%s`: %s",
          field.getName(),
          model.getClass().getName(),
          ex.getMessage()),
        ex);
    }
  }

  /**
   * Checks if the provided value for the specified field of the specified model matches this
   * criterion.
   *
   * @param   currentValue
   *          The current value of the field in the model.
   * @param   model
   *          The model being inspected. This is provided for context, but sub-classes are not
   *          required to have to work with the model directly.
   * @param   field
   *          The field being inspected in the model. This is provided for context, but sub-classes
   *          are not required to have to work with the model directly.
   *
   * @return  {@code true} if the specified {@code currentValue} matches this criterion --
   *          optionally taking the {@code model} or {@code field} into account; or,
   *          {@code false} if the value does not match.
   */
  protected abstract boolean valueMatches(final Object currentValue, final M model,
                                          final Field field);

  /**
   * Gets the field in the model that will be checked against this criterion.
   *
   * @return  The reflection field for the field in the model.
   */
  protected Field getTargetField() {
    return this.targetField;
  }
}
