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

package com.rosieapp.services.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.NewModelIdentifier;

/**
 * Common interface for Rosie JSON API service models.
 */
public interface Model {
  /**
   * Assigns this model an identifier.
   *
   * <p>A given instance of a model may only be assigned an identifier once. The model must not
   * already have an existing object identifier set, with the exception that the model may have an
   * identifier of type {@link NewModelIdentifier}.
   *
   * <p>This method is typically invoked only by the model's builder and by methods that handle
   * persistence of a model for the first time.
   *
   * <p>If the model already has an ID equivalent to the ID being provided, this method has no
   * effect.
   *
   * @param   id
   *          The new ID for this object.
   *
   * @throws  IllegalStateException
   *          If this model already has an identifier set, and the identifier does not represent
   *          a new object identifier.
   */
  void assignId(ModelIdentifier id);

  /**
   * Gets an immutable identifier for this object.
   *
   * @return  The identifier for this object, which is typically something like a UUID or long value
   *          primary key.
   */
  @JsonIgnore
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
