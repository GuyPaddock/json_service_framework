/*
 * Copyright (c) 2018 Rosie Applications, Inc.
 */

package com.rosieapp.util.stream;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Re-usable utility methods for dealing with stream collectors.
 *
 * <p>This expands on functionality provided by {@link java.util.stream.Collectors}.
 */
public final class Collectors {
  /**
   * Private constructor for singleton utility class.
   */
  private Collectors() {
  }

  /**
   * Returns a {@code Collector} that accumulates elements into a {@code LinkedHashMap} whose keys
   * and values are the result of applying the provided mapping functions to the input elements.
   *
   * <p>If the mapped keys contains duplicates (according to {@link Object#equals(Object)}), an
   * {@code IllegalStateException} is thrown when the collection operation is performed.
   *
   * <p>The returned {@code Collector} is not concurrent.
   *
   * @param   <T>
   *          The type of the input elements.
   * @param   <K>
   *          The type returned by the key mapping function.
   * @param   <U>
   *          The type returned by the value mapping function.
   * @param   keyMapper
   *          A function that obtains a unique key for each input element.
   * @param   valueMapper
   *          A function that obtains the value to use for each input element.
   *
   * @return  A {@code Collector} that collects elements into a {@code LinkedHashMap},
   *          where the key of each entry is the result of applying the keyMapper to each input
   *          element, and the value of each entry is the result of applying the valueMapper to the
   *          corresponding input element.
   *
   * @throws  IllegalStateException
   *          If two input elements map to the same key.
   */
  @SuppressWarnings("squid:S1452") // Have to follow contract of java.util.stream.Collectors.toMap()
  public static <T, K, U>
  Collector<T, ?, Map<K, U>> toLinkedMap(final Function<? super T, ? extends K> keyMapper,
                                         final Function<? super T, ? extends U> valueMapper)
  throws IllegalStateException {
    final Collector<T, ?, Map<K, U>> collector;

    collector =
      java.util.stream.Collectors.toMap(
        keyMapper,
        valueMapper,
        (oldValue, newValue) -> {
          // Would love to improve this in Java 9, since it fixes JDK-8040892
          // which would actually give us access to the key.
          throw new IllegalStateException("Duplicate key encountered");
        },
        LinkedHashMap::new);

    return collector;
  }
}
