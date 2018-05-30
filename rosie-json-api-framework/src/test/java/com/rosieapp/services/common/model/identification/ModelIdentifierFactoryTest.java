package com.rosieapp.services.common.model.identification;

import com.greghaskins.spectrum.Spectrum;
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
      context("when the value provided is a number", () -> {
        it("returns a LongIdentifier with the provided value", () -> {

          ModelIdentifier id = ModelIdentifierFactory.valueOf("123");
          assertThat(id).isInstanceOf(LongIdentifier.class);
        });
      });
      context("when the value provided is a negative number",() -> {
        it("throws an IllegalArgumentException", () -> {

          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            ModelIdentifier id;
            id = ModelIdentifierFactory.valueOf("-5");
          }).withMessage("Unrecognized identifier format: -5");
        });
      });
      context("context",() -> {
        it("behavior", () -> {

        });
      });
      context("context",() -> {
        it("behavior", () -> {

        });
      });
      context("context",() -> {
        it("behavior", () -> {

        });
      });
    });
  }

}
