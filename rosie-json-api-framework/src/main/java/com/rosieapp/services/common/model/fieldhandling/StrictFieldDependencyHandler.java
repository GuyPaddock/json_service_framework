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

package com.rosieapp.services.common.model.fieldhandling;

import java.util.Optional;

/**
 * A field dependency handler that handles missing required field values by throwing an
 * exception.
 *
 * @see LaxFieldDependencyHandler
 */
public class StrictFieldDependencyHandler
extends AbstractFieldDependencyHandler {
  /**
   * Validates and returns the value to use when populating the specified required field.
   *
   * <p>If the field value is {@code null}, the handler will communicate this by raising a
   * {@link RequiredFieldMissingException}.
   *
   * {@inheritDoc}
   *
   * @return  The non-null value to use for the field.
   *
   * @throws  RequiredFieldMissingException
   *          If the required field value is {@code null}.
   */
  @Override
  public <F> F handleRequiredField(final F fieldValue, final String fieldName)
  throws RequiredFieldMissingException {
    return Optional.ofNullable(fieldValue)
      .orElseThrow(
        () -> new RequiredFieldMissingException(fieldName));
  }
}
