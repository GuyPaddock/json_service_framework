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

package com.rosieapp.util;

/**
 * A class for common utility methods needed when dealing with Strings.
 */
public final class Strings {
  /**
   * Private constructor for singleton utility class.
   */
  private Strings() {
  }

  /**
   * Converts a string into a format that is suitable for being safely run through
   * {@link java.text.MessageFormat#format(Object)}.
   *
   * <p>This is typically required if curly braces (e.g. <code>{</code> or <code>}</code>) will be
   * included within the string, as these characters usually indicate the start of argument
   * references for {@code MessageFormat}.
   *
   * @param   string
   *          The string to escape.
   *
   * @return  The escaped string.
   */
  public static String escapeForMessageFormat(final String string) {
    return string.replaceAll("([{}])", "'$1'");
  }
}
