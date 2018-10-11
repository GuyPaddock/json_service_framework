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
 * Exception thrown by a validating {@link FieldDependencyHandler} when a required field is missing
 * a value.
 */
public class RequiredFieldMissingException
extends RuntimeException {
  /**
   * Constructor for {@code RequiredFieldMissingException}.
   *
   * @param fieldName
   *        The name of the field that was required but was missing.
   */
  public RequiredFieldMissingException(final String fieldName) {
    super(
      String.format(
        "`%s` is a required field that has not been provided with a value", fieldName));
  }
}
