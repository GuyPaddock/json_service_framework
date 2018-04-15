/*
 * Copyright (c) 2018 Rosie Applications Inc. All rights reserved.
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
