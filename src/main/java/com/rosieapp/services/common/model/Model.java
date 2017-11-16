package com.rosieapp.services.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rosieapp.services.common.model.identifiers.ModelIdentifier;

/**
 * Common interface for Rosie JSON API service models.
 */
public interface Model {
  /**
   * Gets an immutable identifier for this object.
   *
   * @return  The identifier for this object, which is typically something like a UUID or long value
   *          primary key.
   */
  ModelIdentifier getId();

  /**
   * Determines whether or not this model has been persisted to a source of record, or is
   * newly-created and exists only in memory.
   *
   * @return  {@code true} if this model is a new, un-persisted record; or, {@code false},
   *          otherwise.
   */
  @JsonIgnore
  boolean isNew();
}
