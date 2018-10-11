/*
 * Copyright (c) 2018 Rosie Applications Inc.
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
      final Supplier<List<String>> values =
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
