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

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
public class LaxFieldDependencyHandlerTest {
  {
    final Supplier<FieldDependencyHandler> subject = let(() -> new LaxFieldDependencyHandler());

    describe("#handleOptionalField", () -> {
      final Variable<String> fieldValue   = new Variable<>();
      final Variable<String> defaultValue = new Variable<>();

      final Supplier<String> result = let(() -> {
        return subject.get().handleOptionalField(fieldValue.get(), "testField", defaultValue.get());
      });

      context("when the field value is `null`", () -> {
        beforeEach(() -> {
          fieldValue.set(null);
        });

        context("when the default field value is `null`", () -> {
          beforeEach(() -> {
            defaultValue.set(null);
          });

          it("returns `null`", () -> {
            assertThat(result.get()).isNull();
          });
        });

        context("when the default field value is not `null`", () -> {
          beforeEach(() -> {
            defaultValue.set("default value");
          });

          it("returns the default value", () -> {
            assertThat(result.get()).isEqualTo("default value");
          });
        });
      });

      context("when the field value is not `null`", () -> {
        beforeEach(() -> {
          fieldValue.set("field value");
        });

        context("when the default field value is `null`", () -> {
          beforeEach(() -> {
            defaultValue.set(null);
          });

          it("returns the field value", () -> {
            assertThat(result.get()).isEqualTo("field value");
          });
        });

        context("when the default field value is not `null`", () -> {
          beforeEach(() -> {
            defaultValue.set("default value");
          });

          it("returns the field value", () -> {
            assertThat(result.get()).isEqualTo("field value");
          });
        });
      });
    });

    describe("#handleRequiredField", () -> {
      final Variable<String> fieldValue = new Variable<>();

      final Supplier<String> result =
        let(() -> subject.get().handleRequiredField(fieldValue.get(), "testField"));

      context("when the field value is `null`", () -> {
        beforeEach(() -> {
          fieldValue.set(null);
        });

        it("does not throw an error and returns `null`", () -> {
          assertThat(result.get()).isNull();
        });
      });

      context("when the field value is not `null`", () -> {
        beforeEach(() -> {
          fieldValue.set("field value");
        });

        it("returns the field value", () -> {
          assertThat(result.get()).isEqualTo("field value");
        });
      });
    });
  }
}
