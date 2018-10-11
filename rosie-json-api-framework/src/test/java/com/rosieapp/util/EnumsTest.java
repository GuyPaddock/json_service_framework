/*
 * Copyright (c) 2017-2018 Rosie Applications Inc.
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

package com.rosieapp.util;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.greghaskins.spectrum.Spectrum;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class EnumsTest {
  {
    describe(".findValueOrThrow", () -> {
      context("when given an Enum that has no values", () -> {
        final Supplier<Class<EmptyEnum>> enumClass = let(() -> EmptyEnum.class);

        it("throws an IllegalArgumentException", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              Enums.findValueOrThrow(enumClass.get(), (value) -> true);
            })
            .withMessage(
              "No `com.rosieapp.util.EnumsTest.EmptyEnum` was found that matched the specified "
              + "filter.")
            .withNoCause();
        });
      });

      context("when given an Enum that has values", () -> {
        final Supplier<Class<Colors>> enumClass = let(() -> Colors.class);

        context("when the predicate does not match any of the values", () -> {
          final Supplier<Predicate<Colors>> predicate = let(() -> (color) -> false);

          it("throws an IllegalArgumentException", () -> {
            assertThatExceptionOfType(IllegalArgumentException.class)
              .isThrownBy(() -> {
                Enums.findValueOrThrow(enumClass.get(), predicate.get());
              })
              .withMessage(
                "No `com.rosieapp.util.EnumsTest.Colors` was found that matched the specified "
                + "filter.")
              .withNoCause();
          });
        });

        context("when the predicate matches one of the values", () -> {
          final Supplier<Predicate<Colors>> predicate =
            let(() -> (color) -> color.name().equals("WHITE"));

          it("returns the matching value", () -> {
            assertThat(Enums.findValueOrThrow(enumClass.get(), predicate.get()))
              .isEqualTo(Colors.WHITE);
          });
        });

        context("when the predicate matches multiple values", () -> {
          final Supplier<Predicate<Colors>> predicate =
            let(() -> (color) -> !Arrays.asList("RED", "WHITE").contains(color.name()));

          it("returns the first matching value, according to the order within the enum", () -> {
            assertThat(Enums.findValueOrThrow(enumClass.get(), predicate.get()))
              .isEqualTo(Colors.BLUE);
          });
        });
      });
    });
  }

  private enum EmptyEnum {
  }

  private enum Colors {
    RED,
    WHITE,
    BLUE,
    GREEN
  }
}
