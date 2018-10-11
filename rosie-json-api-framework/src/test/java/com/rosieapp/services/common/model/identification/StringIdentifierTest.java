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

package com.rosieapp.services.common.model.identification;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.greghaskins.spectrum.Spectrum;
import java.util.Optional;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr",
  "ConstantConditions"
})
public class StringIdentifierTest {
  {
    describe("constructor", () -> {
      context("when the provided value is empty", () -> {
        it("returns a new StringIdentifier with an empty value", () -> {
          StringIdentifier id = new StringIdentifier("");

          assertThat(id.getValue()).isEqualTo("");
        });
      });

      context("when a null value is provided", () -> {
        it("throws a NullPointerException", () -> {
          assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            new StringIdentifier(null);
          }).withMessage("value cannot be null");
        });
      });

      context("when a non empty string is provided", () -> {
        it("returns a new StringIdentifier with the provided value", () -> {
          StringIdentifier id = new StringIdentifier("abc-123");

          assertThat(id.getValue()).isEqualTo("abc-123");
        });
      });
    });

    describe("#createFrom", () -> {
      context("when the provided value is null", () -> {
        it("returns an empty optional", () -> {
          Optional<ModelIdentifier> id = StringIdentifier.createFrom(null);

          assertThat(id).isEmpty();
        });
      });

      context("when the provided value is empty", () -> {
        it("returns a ModelIdentifier with an empty value", () -> {
          Optional<ModelIdentifier> id = StringIdentifier.createFrom("");

          assertThat(id.get().toString()).isEqualTo("");
        });
      });

      context("when a non empty value is provided", () -> {
        it("returns a ModelIdentifier with the provided value", () -> {
          Optional<ModelIdentifier> id = StringIdentifier.createFrom("abc-123");

          assertThat(id.get().toString()).isEqualTo("abc-123");
        });
      });
    });

    describe("#equals", () -> {
      context("when both StringIdentifiers are the same", () -> {
        it("returns true", () -> {
          StringIdentifier id = new StringIdentifier("abc-123");

          //noinspection EqualsWithItself
          assertThat(id.equals(id)).isTrue();
        });
      });

      context("when both StringIdentifiers have the same value", () -> {
        it("returns true", () -> {
          StringIdentifier first = new StringIdentifier("abc-123");
          StringIdentifier second = new StringIdentifier("abc-123");

          assertThat(first.equals(second)).isTrue();
        });
      });

      context("when comparing a StringIdentifier to a string with the same value", () -> {
        it("returns false", () -> {
          StringIdentifier id = new StringIdentifier("abc-123");

          //noinspection EqualsBetweenInconvertibleTypes
          assertThat(id.equals("abc-123")).isFalse();
        });
      });

      context("when the StringIdentifiers have different values", () -> {
        it("returns false", () -> {
          StringIdentifier first = new StringIdentifier("abc");
          StringIdentifier second = new StringIdentifier("abc-123");

          assertThat(first.equals(second)).isFalse();
        });
      });
    });
  }
}
