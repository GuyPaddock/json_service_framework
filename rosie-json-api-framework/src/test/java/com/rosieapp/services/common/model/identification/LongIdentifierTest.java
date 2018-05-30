package com.rosieapp.services.common.model.identification;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.greghaskins.spectrum.Spectrum;
import java.util.Optional;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class LongIdentifierTest {

  {
    describe("#createFrom", () -> {
      context("when the provided value is not a valid number", () -> {
        it("returns an empty optional", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom("one");
          assertThat(id).isEmpty();
        });
      });
      context("when the provided value is empty", () -> {
        it("returns an empty optional", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom("");
          assertThat(id).isEmpty();
        });
      });
      context("when the value provided is a negative number", () -> {
        it("returns an empty optional", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom("-10");
          assertThat(id).isEmpty();
        });
      });
      context("when the provided value is null", () -> {
        it("returns an empty optional", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom(null);
          assertThat(id).isEmpty();
        });
      });
      context("when the value provided is a number greater than 0",( ) -> {
        it("returns a ModelIdentifier with the provided value", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom("123");
          assertThat(id.get().toString()).isEqualTo("123");
        });
      });
      context("when the provided value is larger than a Long", () -> {
        it("returns an empty optional", () -> {

          Optional<ModelIdentifier> id = LongIdentifier.createFrom("9223372036854775808");
          assertThat(id).isEmpty();
        });
      });
    });
    describe("constructors", () -> {
      context("when the provided value is a negative number", () -> {
        it("throws an IllegalArgumentException", () -> {

          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            LongIdentifier id = new LongIdentifier(-5);
          }).withMessage("value must be greater than 0");
        });
      });
      context("when the provided value is a long", () -> {
        it("creates a new LongIdentifier with the provided value", () -> {

          LongIdentifier id = new LongIdentifier(123);
          assertThat(id.getValue()).isEqualTo(123);
          assertThat(id.toString()).isEqualTo("123");
        });
      });
    });
    describe("#equals", () -> {
      context("when both LongIdentifiers are the same", () -> {
        it("returns true", () -> {

          LongIdentifier id = new LongIdentifier(123);
          assertThat(id.equals(id)).isTrue();
        });
      });
      context("when both LongIdentifiers have the same value", () -> {
        it("returns true", () -> {

          LongIdentifier first = new LongIdentifier(123);
          LongIdentifier second = new LongIdentifier(123);
          assertThat(first.equals(second)).isTrue();
        });
      });
      context("when comparing a LongIdentifier and a long with the same value", () -> {
        it("returns false", () -> {

          LongIdentifier id = new LongIdentifier(123);
          assertThat(id.equals(123)).isFalse();
        });
      });
      context("when comparing a LongIdentifier and a ModelIdentifier with the same value", () -> {
        it("returns true", () -> {

          LongIdentifier longId = new LongIdentifier(123);
          Optional<ModelIdentifier> modelId = LongIdentifier.createFrom("123");

          assertThat(longId.equals(modelId.get())).isTrue();
        });
      });
    });
  }

}
