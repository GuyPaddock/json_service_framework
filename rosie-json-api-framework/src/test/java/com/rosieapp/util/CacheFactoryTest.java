/*
 * Copyright (c) 2018 Rosie Applications, Inc.
 */

package com.rosieapp.util;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.replace;
import static org.powermock.reflect.Whitebox.invokeMethod;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.testing.FakeTicker;
import com.greghaskins.spectrum.Spectrum;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Spectrum.class)
@PrepareForTest(CacheBuilder.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "Convert2MethodRef"
})
public class CacheFactoryTest {
  {
    describe(".createSmallShortTermCache()", () -> {
      it("creates a cache that holds no more than 32 items", () -> {
        final Cache<Integer, String> cache = CacheFactory.createSmallShortTermCache();

        cache.put(1, "Happy value 1");

        assertThat(cache.getIfPresent(1)).isNotNull();

        IntStream.rangeClosed(2, 32).forEach((value) -> {
          cache.put(value, "Happy value " + value);
        });

        assertThat(cache.getIfPresent(33)).isNull();

        cache.put(33, "Happy value 33");

        assertThat(cache.getIfPresent(1)).isNull();
        assertThat(cache.getIfPresent(33)).isNotNull();
      });

      it("creates a cache that expires entries after 10 minutes", () -> {
        final Cache<Integer, String>  cache;
        final FakeTicker              fakeTicker = new FakeTicker();

        // Inject additional behavior around the build() method on the builder, so we can insert
        // our own ticker into the context of the builder to control the perception of time in the
        // cache.
        replace(method(CacheBuilder.class, "build"))
          .with((builder, method, arguments) -> {
            // Equivalent to: builder.ticker(fakeTicker)
            invokeMethod(builder, "ticker", fakeTicker);

            return method.invoke(builder, arguments);
          });

        cache = CacheFactory.createSmallShortTermCache();

        cache.put(1, "Happy value 1");
        assertThat(cache.getIfPresent(1)).isNotNull();

        fakeTicker.advance(9,  TimeUnit.MINUTES);
        fakeTicker.advance(59, TimeUnit.SECONDS);

        assertThat(cache.getIfPresent(1)).isNotNull();

        fakeTicker.advance(1, TimeUnit.SECONDS);

        assertThat(cache.getIfPresent(1)).isNull();
      });
    });
  }
}
