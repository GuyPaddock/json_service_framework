package com.rosieapp.util.stream;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;

import com.greghaskins.spectrum.Spectrum;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "Convert2MethodRef"
})
public class CollectorsTest {
  {
    describe(".toLinkedMap", () -> {
      Supplier<List<String>> values =
        let(() -> Arrays.asList("whiskey", "tango", "foxtrot", "alpha"));

      it("returns a collector that maps input elements using a map that respects order", () -> {
        final Map<String, Integer>  finalMap;
        final AtomicInteger         counter = new AtomicInteger();

        finalMap =
          values
            .get()
            .stream()
            .collect(
              Collectors.toLinkedMap(
                Function.identity(),
                (_value) -> counter.getAndIncrement()));

        assertThat(finalMap.keySet()).containsSequence("whiskey", "tango", "foxtrot", "alpha");
      });
    });
  }
}
