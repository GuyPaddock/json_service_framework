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
import java.util.UUID;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr",
  "ConstantConditions"
})
// CHECKSTYLE IGNORE AbbreviationAsWordInName
public class UUIDIdentifierTest {
  {
    describe("constructors", () -> {
      context("when no value is provided", () -> {
        it("returns a new UUIDIdentifier with a random UUID", () -> {
          UUIDIdentifier first = new UUIDIdentifier();
          UUIDIdentifier second = new UUIDIdentifier();

          assertThat(first.getValue()).isNotEqualTo(second.getValue());
        });
      });

      context("when a non empty string that is not a valid uuid is provided", () -> {
        it("throws an IllegalArgumentException", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new UUIDIdentifier("abc-123");
          });
        });
      });

      context("when an empty string is provided", () -> {
        it("throws an IllegalArgumentException", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new UUIDIdentifier("");
          });
        });
      });

      context("when a null value is provided", () -> {
        it("throws a NullPointerException", () -> {
          assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            new UUIDIdentifier((UUID)null);
          }).withMessage("value cannot be null");
        });
      });

      context("when a valid string is provided", () -> {
        it("returns a new UUIDIdentifier with the provided value", () -> {
          UUIDIdentifier id = new UUIDIdentifier("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");

          assertThat(id.getValue().toString()).isEqualTo("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
        });
      });

      context("when a UUID is provided", () -> {
        it("returns a new UUIDIdentifier with the provided value", () -> {
          UUID uuid = UUID.fromString("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
          UUIDIdentifier id = new UUIDIdentifier(uuid);

          assertThat(id.getValue()).isEqualTo(uuid);
        });
      });
    });

    describe("#createFrom", () -> {
      context("when a non empty string that is not a valid uuid is provided", () -> {
        it("returns an empty optional", () -> {
          Optional<ModelIdentifier> id = UUIDIdentifier.createFrom("abc-123");

          assertThat(id).isEmpty();
        });
      });

      context("when the provided value is empty", () -> {
        it("returns an empty optional", () -> {
          Optional<ModelIdentifier> id = UUIDIdentifier.createFrom("");

          assertThat(id).isEmpty();
        });
      });

      context("when the provided value is null", () -> {
        it("throws a null pointer exception", () -> {
          assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            UUIDIdentifier.createFrom(null);
          }).withMessage("value cannot be null");
        });
      });

      context("when a non empty valid uuid is provided", () -> {
        it("returns a model identifier with the provided value", () -> {
          Optional<ModelIdentifier> id =
              UUIDIdentifier.createFrom("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");

          assertThat(id.get().toString()).isEqualTo("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
        });
      });
    });

    describe("#equals", () -> {
      context("when comparing the same object", () -> {
        it("returns true", () -> {
          UUIDIdentifier id = new UUIDIdentifier();

          //noinspection EqualsWithItself
          assertThat(id.equals(id)).isTrue();
        });
      });

      context("when both UUIDIdentifiers have the same value", () -> {
        it("returns true", () -> {
          UUIDIdentifier first = new UUIDIdentifier("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
          UUIDIdentifier second = new UUIDIdentifier("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");

          assertThat(first.equals(second)).isTrue();
        });
      });

      context("when comparing a UUIDIdentifiers to a string with the same value", () -> {
        it("returns false", () -> {
          UUIDIdentifier id = new UUIDIdentifier("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");

          //noinspection EqualsBetweenInconvertibleTypes
          assertThat(id.equals("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c")).isFalse();
        });
      });

      context("when the UUIDIdentifiers have different values", () -> {
        it("returns false", () -> {
          UUIDIdentifier first = new UUIDIdentifier("9999903c-b3a5-42a4-b95a-795d3bee8b99");
          UUIDIdentifier second = new UUIDIdentifier("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");

          assertThat(first.equals(second)).isFalse();
        });
      });
    });
  }
}
