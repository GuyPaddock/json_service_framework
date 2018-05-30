package com.rosieapp.services.common.model.identification;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class NewModelIdentifierTest {

  {
    describe("#getInstance", () -> {
      it("always returns the same instance", () -> {
        NewModelIdentifier first = NewModelIdentifier.getInstance();
        NewModelIdentifier second = NewModelIdentifier.getInstance();

        assertThat(first).isSameAs(second);
      });
    });
    describe("#toString", () -> {
      it("returns null", () -> {
        String value = NewModelIdentifier.getInstance().toString();
        assertThat(value).isNull();
      });
    });
    describe("#isObjectNew", () -> {
      it("returns true", () -> {
        assertThat(NewModelIdentifier.getInstance().isObjectNew()).isTrue();
      });
    });
  }

}
