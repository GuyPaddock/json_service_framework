/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.common.collections;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Re-usable utility methods for dealing with maps (i.e. {@link Map}).
 */
@SuppressWarnings("PMD.ShortClassName")
public final class Maps {
  /**
   * Private constructor; this is a static class.
   */
  private Maps() {
  }

  /**
   * Converts all of the entries of a map to a string.
   *
   * <p>The output is formatted more nicely than the default {@link AbstractMap#toString()}
   * implementation in the JDK. Keys and values are wrapped in quotes, and the values are presented
   * in alphabetic order by key.
   *
   * @param   map
   *          The map to convert to a string.
   * @param   <K>
   *          The type of key being used in the map.
   * @param   <V>
   *          The type of value being used in the map.
   *
   * @return  The string representation of the map.
   */
  public static <K extends Comparable<K>, V> String toString(final Map<K, V> map) {
    return toString(map.entrySet().stream());
  }

  /**
   * Converts a stream of map entries to a string.
   *
   * <p>This overload is provided for cases in which information from multiple maps is being
   * combined into the same output.
   *
   * <p>Keys and values are wrapped in quotes in the resulting string, and the values are presented
   * in alphabetic order by key.
   *
   * @param   entryStream
   *          The stream of map entries to convert to a string.
   * @param   <K>
   *          The type of key being used in the map.
   * @param   <V>
   *          The type of value being used in the map.
   *
   * @return  The string representation of the map.
   */
  public static <K extends Comparable<K>, V> String toString(final Stream<Entry<K, V>> entryStream) {
    final String string =
      entryStream
        .sorted(Map.Entry.comparingByKey())
        .map(
          (entry) -> String.format("\"%s\": \"%s\"", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining(", "));

    return string;
  }
}
