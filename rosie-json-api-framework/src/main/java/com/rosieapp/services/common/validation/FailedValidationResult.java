/*
 * Copyright (c) 2018 Rosie Applications, Inc.
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
