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
 * Optional, abstract parent class provided for use by identifiers of models that have been
 * persisted.
 *
 * <p>This implementation provides built-in handling for the {@link #isObjectNew()} method.
 */
public abstract class PersistedModelIdentifier
extends AbstractModelIdentifier {
  @Override
  public final boolean isObjectNew() {
    return false;
  }
}
