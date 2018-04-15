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
  public static void requireCanonicalName(Class<?> target)
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
