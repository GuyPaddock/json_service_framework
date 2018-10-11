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
 * Optional, abstract parent class provided for use by model identifiers in the system.
 *
 * <p>This implementation ensures that sub-classes provide an implementation of {@link #toString()}.
 */
public abstract class AbstractModelIdentifier
implements ModelIdentifier {
  @Override
  public abstract String toString();

  @Override
  public abstract boolean equals(Object other);

  @Override
  public abstract int hashCode();
}
