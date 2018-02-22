/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.util;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Common utility methods for dealing with enumerated types ("enums").
 */
public final class Enums {
  /**
   * Private constructor for singleton utility class.
   */
  private Enums() {
  }

  /**
   * Applies a filter to a set of enum values to locate one that matches, or throwing an exception
   * if none of the values match.
   *
   * <p>This implementation is best for cases in which the enum that corresponds to a value is
   * always expected to be present, and it is an error if it is not. In such cases, when this method
   * throws an {@link IllegalArgumentException}, that signifies a defect in code that must be
   * corrected.
   *
   * <p>For efficiency, the first value that matches the filter is returned immediately, and the
   * remaining values are not evaluated. The caller must guarantee either that the filter only
   * uniquely matches one value in the enum; or that the order values have been declared in the enum
   * will ensure the first of several matching values will cause the desired value to be returned
   * first.
   *
   * @param   enumClass
   *          The type of enum to search.
   * @param   filter
   *          The predicate that will be invoked to examine each enum value. The predicate must
   *          return {@code true} if the enum value matches the desired criteria, and {@code false}
   *          if the enum value does not match.
   *
   * @param   <E>
   *          The type of enum.
   *
   * @return  The first enum value encountered that matches the filter.
   *
   * @throws  IllegalArgumentException
   *          If none of the values in the enum matched the filter.
   */
  public static <E extends Enum<E>> E findValueOrThrow(final Class<E> enumClass,
                                                       final Predicate<E> filter)
  throws IllegalArgumentException {
    final E result;

    result =
      EnumSet.allOf(enumClass).stream()
        .filter(filter)
        .findFirst()
        .orElseThrow(
          () -> new IllegalArgumentException(
            MessageFormat.format(
              "No `{0}` was found that matched the specified filter.",
              enumClass.getCanonicalName())));

    return result;
  }
}
