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

/**
 * A field dependency handler that addresses missing required field values by merely returning
 * {@code null}, instead of throwing an exception.
 *
 * @see StrictFieldDependencyHandler
 */
public class LaxFieldDependencyHandler
extends AbstractFieldDependencyHandler {
  /**
   * Returns the value to use when populating the specified required field.
   *
   * <p>If the field value is {@code null}, this handler simply supplies {@code null} in place of
   * the missing value.
   *
   * {@inheritDoc}
   *
   * @return  Either the value to use for the field; or {@code null}, if {@code fieldValue} is
   *          {@code null}.
   */
  @Override
  public <F> F handleRequiredField(final F fieldValue, final String fieldName) {
    return this.handleOptionalField(fieldValue, fieldName, null);
  }
}
