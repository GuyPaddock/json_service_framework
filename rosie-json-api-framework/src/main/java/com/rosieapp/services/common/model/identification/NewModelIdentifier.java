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
 * An identifier that is used for newly-created models that have not yet been persisted.
 */
public final class NewModelIdentifier
extends AbstractModelIdentifier {
  /**
   * A singleton instance of this identifier.
   */
  private static final NewModelIdentifier INSTANCE = new NewModelIdentifier();

  /**
   * Gets the singleton, flyweight instance of this identifier.
   *
   * <p>For efficiency, this is preferred to using the constructor since every instance has no
   * instance state and behaves exactly the same way.
   *
   * @return The identifier instance.
   */
  public static NewModelIdentifier getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor for {@link NewModelIdentifier}.
   */
  private NewModelIdentifier() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code true}, always (since this identifier is only used for new objects).
   */
  @Override
  public boolean isObjectNew() {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code null}, to signify that this identifies a new, un-persisted object.
   */
  @Override
  public String toString() {
    return null;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(final Object other) {
    return (this == other);
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }
}
