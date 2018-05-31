package com.rosieapp.services.common.model.identification;

import com.greghaskins.spectrum.Spectrum;
import java.util.UUID;
import org.junit.runner.RunWith;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(Spectrum.class)
public class ModelIdentifierFactoryTest {

  {
    describe("#getInstance", () -> {
      it("returns the same instance each time", () -> {
        ModelIdentifierFactory factory = ModelIdentifierFactory.getInstance();
        ModelIdentifierFactory otherFactory = ModelIdentifierFactory.getInstance();

        assertThat(factory).isSameAs(otherFactory);
      });
    });
    describe("#valueOf", () -> {
      context("when the value provided is a string representing a number", () -> {
        it("returns a LongIdentifier with the provided value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf("123");
          assertThat(id).isInstanceOf(LongIdentifier.class);
          assertThat(id.toString()).isEqualTo("123");
        });
      });
      context("when the value provided is a string representing a negative number",() -> {
        it("returns a StringIdentifier with the provided value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf("-5");
          assertThat(id).isInstanceOf(StringIdentifier.class);
          assertThat(id.toString()).isEqualTo("-5");
        });
      });
      context("when the value provided is a string representing a uuid",() -> {
        it("returns a UUIDIdentifier with the provided value", () -> {

          ModelIdentifier id =
              ModelIdentifierFactory.valueOf("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
          assertThat(id).isInstanceOf(UUIDIdentifier.class);
          assertThat(id.toString()).isEqualTo("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
        });
      });
      context("when the value provided is an empty string",() -> {
        it("returns a StringIdentifier with an empty string as its value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf("");
          assertThat(id).isInstanceOf(StringIdentifier.class);
          assertThat(id.toString()).isEqualTo("");
        });
      });
      context("when the value provided is null",() -> {
        it("throws a NullPointerException", () -> {

          assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            String value = null;
            ModelIdentifierFactory.valueOf(value);
          });
        });
      });
      context("when the value provided is a uuid", () -> {
        it("returns a UUIDIdentifier with the provided value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf(
              UUID.fromString("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c"));
          assertThat(id.toString()).isEqualTo("8fe6b03c-b3a5-42a4-b95a-795d3bee8b1c");
        });
      });
      context("when the value provided is a number", () -> {
        it("returns a LongIdentifier with the provided value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf(123);
          assertThat(id.toString()).isEqualTo("123");
        });
      });
      context("when the value provided is a negative number", () -> {
        it("returns an IllegalArgumentException", () -> {

          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            ModelIdentifierFactory.valueOf(-5);
          });
        });
      });
    });
  }

}
