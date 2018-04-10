/*
 * Copyright (c) 2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.validation;

/**
 * The type of validation result returned when a value passes validation.
 */
public class SuccessfulValidationResult
implements ValidationResult {
  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public String toString() {
    return "";
  }
}
