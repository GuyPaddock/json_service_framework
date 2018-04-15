/*
 * Copyright (c) 2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 * A small utility class for instantiating in-memory caches.
 */
public final class CacheFactory {
  /**
   * Creates a cache that stores no more than 32 entries at a time, and evicts entries after 10
   * minutes of use.
   *
   * <p>This can provide a small boost in performance over a short period of time, while minimizing
   * memory usage.
   *
   * @param   <K>
   *          The type of value being used for cache keys.
   * @param   <V>
   *          The type of value being used for cache values.
   *
   * @return  A new cache instance.
   */
  public static <K, V> Cache<K, V> createSmallShortTermCache() {
    Cache<K, V> cache;

    cache =
      CacheBuilder
        .newBuilder()
        .maximumSize(32)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    return cache;
  }

  /**
   * Private constructor for singleton utility class.
   */
  private CacheFactory() {
  }
}
