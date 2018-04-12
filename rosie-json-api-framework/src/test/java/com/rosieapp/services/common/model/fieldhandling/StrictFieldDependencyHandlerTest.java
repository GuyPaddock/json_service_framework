package com.rosieapp.services.common.model.fieldhandling;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
public class StrictFieldDependencyHandlerTest {
  {
    final Supplier<FieldDependencyHandler> subject = let(() -> new StrictFieldDependencyHandler());

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

        it("throws a `RequiredFieldMissingException`", () -> {
          assertThatExceptionOfType(RequiredFieldMissingException.class)
            .isThrownBy(() -> {
              result.get();
            })
            .withMessage("`testField` is a required field that has not been provided with a value")
            .withNoCause();
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
