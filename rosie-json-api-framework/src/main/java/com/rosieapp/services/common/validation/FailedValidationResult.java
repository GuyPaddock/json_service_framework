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
 * The type of validation result returned when a value fails validation.
 *
 * <p>A message describing the issue with the value can be returned by calling {@link #toString()}.
 */
public class FailedValidationResult
implements ValidationResult {
  private final String errorMessage;

  /**
   * Constructor for {@code FailedValidationResult}.
   *
   * @param errorMessage
   *        The error message that indicates why the value failed validation.
   */
  public FailedValidationResult(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public String toString() {
    return this.errorMessage;
  }
}
