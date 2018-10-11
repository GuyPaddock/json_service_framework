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
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr"
})
public class BuilderPopulatedFieldTest {
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

  @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
  private BuilderPopulatedField getAnnotationOnField(final String fieldName) {
    try {
      return AnnotatedClass.class.getField(fieldName).getAnnotation(BuilderPopulatedField.class);
    } catch (NoSuchFieldException ex) {
      throw new RuntimeException(ex);
    }
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
