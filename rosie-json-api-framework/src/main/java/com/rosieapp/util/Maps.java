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
   * in the order they appear in the map.
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
  @SuppressWarnings("UnnecessaryLocalVariable")
  public static <K extends Comparable<K>, V> String toString(
                                                      final Stream<Entry<K, V>> entryStream) {
    final String string =
      entryStream
        .map(
          (entry) -> String.format("\"%s\": \"%s\"", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining(", "));

    return string;
  }

  /**
   * Converts all of the entries of a map to a string, sorted by key.
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
  public static <K extends Comparable<K>, V> String toStringSorted(final Map<K, V> map) {
    return toString(map.entrySet().stream().sorted(Map.Entry.comparingByKey()));
  }
}
