/*
 * Copyright (c) 2018 Rosie Applications Inc. All rights reserved.
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
