/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.identification;

/**
 * Common interface for objects that identify both persisted and un-persisted Rosie JSON API service
 * models.
 */
public interface ModelIdentifier {
  /**
   * Determines whether or not this identifier indicates that the model it corresponds to is new.
   *
   * @return  {@code true} if this identifier marks that the model is a new, un-persisted record;
   *          or, {@code false}, otherwise.
   */
  boolean isObjectNew();

  /**
   * Returns the string representation of this identifier, safe for inclusion in serialization
   * output.
   *
   * @return  The string representation of this identifier.
   */
  @Override
  String toString();
}
