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

package com.rosieapp.services.common.model.fieldhandling;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.google.common.collect.ImmutableMap;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
public class PassthroughPreprocessorTest {
  {
    describe("#preprocessField", () -> {
      final Variable<Object> fieldValue = new Variable<>();

      final Supplier<Field> field = let(() -> mock(Field.class));

      final Supplier<FieldValuePreprocessor> subject = let(() -> new PassthroughPreprocessor());

      final Supplier<Object> result =
        let(() -> subject.get().preprocessField(field.get(), fieldValue.get()));

      context("when given `null`", () -> {
        beforeEach(() -> {
          fieldValue.set(null);
        });

        it("returns `null`", () -> {
          assertThat(result.get()).isNull();
        });
      });

      context("when given an object that does not support cloning", () -> {
        final Supplier<Object> object = let(() -> new Object());

        beforeEach(() -> {
          fieldValue.set(object.get());
        });

        it("returns the same instance it was given", () -> {
          assertThat(result.get()).isSameAs(object.get());
        });
      });

      context("when given an object that supports cloning", () -> {
        final Supplier<Map<String, String>> object =
          let(() -> ImmutableMap.of("key1", "value1"));

        beforeEach(() -> {
          fieldValue.set(object.get());
        });

        it("returns the same instance it was given", () -> {
          assertThat(result.get()).isSameAs(object.get());
        });
      });
    });
  }
}
