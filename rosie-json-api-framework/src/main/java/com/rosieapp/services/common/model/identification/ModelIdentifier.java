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
