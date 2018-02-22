/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.annotation;

/**
 * This annotation marks all objects that currently exist solely to provide workarounds for parts of
 * Rosie's legacy JSON-based service API that are not compliant with the JSON API v1 specification.
 *
 * <p>The long-term goal is to bring all legacy services into compliance with the specification so
 * that all classes tagged with this annotation can be removed in their entirety.
 */
public @interface JSONAPIV1Noncompliant {
  /**
   * Provides the reason for non-compliance.
   *
   * @return  The reason why the object tagged with this annotation is not compliant with the JSON
   *          API v1 specification.
   */
  public String reason();
}
