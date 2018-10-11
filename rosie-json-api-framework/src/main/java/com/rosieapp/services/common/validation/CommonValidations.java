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

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * An enumeration of data validations that are commonly needed for data across services at Rosie.
 *
 * @see #validate(String)
 */
public enum CommonValidations {
  /**
   * Machine names are unique identifiers that are a compromise between being machine-friendly
   * and being human readable.
   *
   * <p>An example of an identifier that is human friendly but not machine friendly is a String
   * containing a combination of spaces, uppercase letters, and lowercase letters
   * (e.g. "My Object"). An example of an identifier that is machine friendly but not human friendly
   * is a UUID or a integer primary key.
   */
  MACHINE_NAME(
    "machine name",
    "\\A[a-z0-9_]+\\z",
    "can only contain underscores, lowercase letters, and the digits 0-9");

  private final String name;
  private final Pattern regex;
  private final String errorMessage;

  /**
   * Constructor for each {@code CommonValidations} enum value.
   *
   * @param name
   *        The human-friendly name of the new format.
   * @param regexString
   *        The regular expression used to validate that a String complies with the new format.
   * @param errorMessage
   *        The message that can be displayed to users or placed into an exception when a string
   *        does not match the new format.
   */
  CommonValidations(final String name, final String regexString, final String errorMessage) {
    this.name         = name;
    this.regex        = Pattern.compile(regexString);
    this.errorMessage = errorMessage;
  }

  /**
   * Apply this validation to the provided value.
   *
   * @param   value
   *          The value to validate.
   *
   * @return  The result of the validation.
   *
   * @see     ValidationResult#isValid()
   * @see     ValidationResult#toString()
   */
  public ValidationResult validate(final String value) {
    final ValidationResult result;

    if (value == null) {
      result =
        new FailedValidationResult(
          MessageFormat.format(
            "Provided value is not a valid {0}: cannot be null",
            this.name));
    } else if (this.regex.matcher(value).matches()) {
      result = new SuccessfulValidationResult();
    } else {
      result =
        new FailedValidationResult(
          MessageFormat.format(
            "`{0}` is not a valid {1}: {2}",
            value, this.name,
            this.errorMessage));
    }

    return result;
  }

  /**
   * Ensures either that the provided argument is valid; or that an exception is thrown with an
   * appropriate message to indicate that it is not.
   *
   * @param   value
   *          The value to validate.
   *
   * @throws  IllegalArgumentException
   *          If the value fails validation.
   */
  public void ensureValidOrThrow(final String value)
  throws IllegalArgumentException {
    final ValidationResult result = this.validate(value);

    if (!result.isValid()) {
      throw new IllegalArgumentException(result.toString());
    }
  }
}
