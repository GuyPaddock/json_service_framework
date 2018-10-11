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

package com.rosieapp.services.common.validation;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatCode;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class CommonValidationsTest {
  {
    describe("#validate", () -> {
      describe("MACHINE_NAME", () -> {
        final Variable<String> inputValue = new Variable<>();

        final Supplier<ValidationResult> result =
          let(() -> CommonValidations.MACHINE_NAME.validate(inputValue.get()));

        context("context when given a `null` value", () -> {
          beforeAll(() -> {
            inputValue.set(null);
          });

          it("returns a `FailedValidationResult`", () -> {
            assertThat(result.get().isValid()).isFalse();
          });

          it("includes an error message in the `FailedValidationResult`", () -> {
            assertThat(result.get().toString()).isEqualTo(
              "Provided value is not a valid machine name: cannot be null");
          });
        });

        context("context when given a string containing spaces", () -> {
          beforeAll(() -> {
            inputValue.set("my string");
          });

          it("returns a `FailedValidationResult`", () -> {
            assertThat(result.get().isValid()).isFalse();
          });

          it("includes an error message in the `FailedValidationResult`", () -> {
            assertThat(result.get().toString()).isEqualTo(
              "`my string` is not a valid machine name: can only contain underscores, lowercase "
              + "letters, and the digits 0-9");
          });
        });

        context("context when given a string containing uppercase letters", () -> {
          beforeAll(() -> {
            inputValue.set("myString");
          });

          it("returns a `FailedValidationResult`", () -> {
            assertThat(result.get().isValid()).isFalse();
          });

          it("includes an error message in the `FailedValidationResult`", () -> {
            assertThat(result.get().toString()).isEqualTo(
              "`myString` is not a valid machine name: can only contain underscores, lowercase "
              + "letters, and the digits 0-9");
          });
        });

        context("context when given a string containing symbols", () -> {
          beforeAll(() -> {
            inputValue.set("my-string");
          });

          it("returns a `FailedValidationResult`", () -> {
            assertThat(result.get().isValid()).isFalse();
          });

          it("includes an error message in the `FailedValidationResult`", () -> {
            assertThat(result.get().toString()).isEqualTo(
              "`my-string` is not a valid machine name: can only contain underscores, lowercase "
              + "letters, and the digits 0-9");
          });
        });

        context("context when given a string containing underscores, lowercase letters, and the "
                + "digits 0-9", () -> {
          beforeAll(() -> {
            inputValue.set("my_string01");
          });

          it("returns a `SuccessfulValidationResult`", () -> {
            assertThat(result.get().isValid()).isTrue();
          });

          it("includes an empty error message in the `SuccessfulValidationResult`", () -> {
            assertThat(result.get().toString()).isEmpty();
          });
        });
      });
    });

    describe("#ensureValidOrThrow", () -> {
      context("when the provided value is not valid", () -> {
        it("throws an IllegalArgumentException containing the validation error", () -> {
          Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(
              () -> CommonValidations.MACHINE_NAME.ensureValidOrThrow("Bad value"))
            .withMessage(
              "`Bad value` is not a valid machine name: can only contain underscores, lowercase "
              + "letters, and the digits 0-9");
        });
      });

      context("when the provided value is valid", () -> {
        it("does not throw any exceptions", () -> {
          assertThatCode(() -> CommonValidations.MACHINE_NAME.ensureValidOrThrow("good_value"))
            .doesNotThrowAnyException();
        });
      });
    });
  }
}
