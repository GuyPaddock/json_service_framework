/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
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
