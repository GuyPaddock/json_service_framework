/*
 * Copyright (c) 2018 Rosie Applications Inc.
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

package com.rosieapp.services.common.validation;

/**
 * An interface for capturing the result of performing a validation.
 */
public interface ValidationResult {
  /**
   * Indicates whether or not validation was successful (i.e. the value was valid).
   *
   * @return  {@code true} if the value passed validation; or
   *          {@code false} if the valid failed validation.
   */
  boolean isValid();

  /**
   * Provides an appropriate error message to display, if a value failed validation.
   *
   * @return  If {@link #isValid()} is {@code false}, this is an error message describing the
   *          situation. If {@code isValid()} is {@code true}, this is an empty string.
   */
  String toString();
}
