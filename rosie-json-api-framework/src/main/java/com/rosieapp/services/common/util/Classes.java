/*
 * Copyright (c) 2018 Rosie Applications Inc.
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

package com.rosieapp.services.common.util;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * A class for common utility methods needed when dealing with classes.
 */
public final class Classes {
  /**
   * Private constructor to ensure this class is static.
   */
  private Classes() {
  }

  /**
   * Requires that the given target class has a canonical name.
   *
   * <p>A class can lack a canonical name if it is declared as an anonymous inner class.
   *
   * @param   target
   *          The class to validate.
   *
   * @throws  IllegalArgumentException
   *          If the class lacks a canonical name.
   */
  public static void requireCanonicalName(final Class<?> target)
  throws IllegalArgumentException {
    final String canonicalName;

    Objects.requireNonNull(target, "target cannot be null");

    canonicalName = target.getCanonicalName();

    if (canonicalName == null) {
      throw new IllegalArgumentException(
        MessageFormat.format(
          "The provided class (`{0}`) does not have a canonical name. This typically indicates "
          + "that the class has been declared as an anonymous inner class, which is not supported.",
          target.getName()));
    }
  }
}
