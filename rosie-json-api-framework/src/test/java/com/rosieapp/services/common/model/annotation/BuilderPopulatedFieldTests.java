package com.rosieapp.services.common.model.annotation;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.fieldhandling.CloningPreprocessor;
import com.rosieapp.services.common.model.fieldhandling.PassthroughPreprocessor;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class BuilderPopulatedFieldTests {
  {
    describe("required", () -> {
      context("when not provided", () -> {
        final Supplier<BuilderPopulatedField> annotation =
          let(() -> getAnnotationOnField("fieldWithDefaults"));

        it("defaults to `false`", () -> {
          assertThat(annotation.get().required()).isFalse();
        });
      });

      context("when provided", () -> {
        final Supplier<BuilderPopulatedField> annotation =
          let(() -> getAnnotationOnField("fieldWithRequiredSet"));

        it("persists the provided value and retains it at run-time", () -> {
          assertThat(annotation.get().required()).isTrue();
        });
      });
    });

    describe("preprocessor", () -> {
      context("when not provided", () -> {
        final Supplier<BuilderPopulatedField> annotation =
          let(() -> getAnnotationOnField("fieldWithDefaults"));

        it("defaults to `CloningPreprocessor`", () -> {
          assertThat(annotation.get().preprocessor()).isEqualTo(CloningPreprocessor.class);
        });
      });

      context("when provided", () -> {
        final Supplier<BuilderPopulatedField> annotation =
          let(() -> getAnnotationOnField("fieldWithPreprocessorSet"));

        it("persists the provided value and retains it at run-time", () -> {
          assertThat(annotation.get().preprocessor()).isEqualTo(PassthroughPreprocessor.class);
        });
      });
    });
  }

  private BuilderPopulatedField getAnnotationOnField(final String fieldName) {
    BuilderPopulatedField result = null;

    try {
      result = AnnotatedClass.class.getField(fieldName).getAnnotation(BuilderPopulatedField.class);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return result;
  }

  private static class AnnotatedClass {
    @BuilderPopulatedField
    public boolean fieldWithDefaults;

    @BuilderPopulatedField(required = true)
    public boolean fieldWithRequiredSet;

    @BuilderPopulatedField(preprocessor = PassthroughPreprocessor.class)
    public boolean fieldWithPreprocessorSet;
  }
}
