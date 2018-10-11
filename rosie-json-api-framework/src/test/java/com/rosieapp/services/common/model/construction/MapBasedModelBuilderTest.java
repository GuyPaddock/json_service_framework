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

package com.rosieapp.services.common.model.construction;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.fieldhandling.FieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.RequiredFieldMissingException;
import com.rosieapp.services.common.model.filtering.ModelFilterBuilder;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
public class MapBasedModelBuilderTest {
  {
    //noinspection Duplicates
    describe("constructors", () -> {
      context("when no field dependency handler is provided", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(TestModelBuilder::new);

        it("is initialized to strictly validate required fields", () -> {
          assertThatExceptionOfType(RequiredFieldMissingException.class)
            .isThrownBy(() -> {
              modelBuilder.get().build();
            });
        });

        it("is initialized not to throw an exception for missing optional fields", () -> {
          assertThatCode(() -> {
            modelBuilder.get().withRequiredField("satisfied").build();
          }).doesNotThrowAnyException();
        });
      });

      context("when a field dependency handler (FDH) is provided", () -> {
        final Supplier<FieldDependencyHandler> dependencyHandler =
          let(() -> mock(FieldDependencyHandler.class));

        final Supplier<ModelBuilder> modelBuilder =
          let(() -> {
            final ModelBuilder result;

            result =
              new TestModelBuilder(dependencyHandler.get())
                .withRequiredField("someRequiredValue")
                .withOptionalField("someOptionalValue");

            return result;
          });

        it("uses the provided FDH to validate required fields", () -> {
          modelBuilder.get().build();

          verify(dependencyHandler.get())
            .handleRequiredField("someRequiredValue", "requiredField");
        });

        it("uses the provided FDH to validate optional fields", () -> {
          modelBuilder.get().build();

          verify(dependencyHandler.get())
            .handleOptionalField("someOptionalValue", "optionalField", "Default");
        });
      });
    });

    describe("#toString()", () -> {
      context("when there are no fields", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(TestModelBuilder::new);

        it("returns `ClassName{\"id\": \"null\"}`", () -> {
          assertThat(modelBuilder.get().toString())
            .isEqualTo(
              "com.rosieapp.services.common.model.construction.MapBasedModelBuilderTest."
              + "TestModelBuilder{\"id\": \"null\"}");
        });
      });

      context("when there is one field", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(() -> {
          final TestModelBuilder result = new TestModelBuilder();

          result.putFieldValue("field1", "value1");

          return result;
        });

        it("returns `ClassName{\"id\": \"null\", \"field1\": \"value1\"}`", () -> {
          assertThat(modelBuilder.get().toString())
            .isEqualTo(
              "com.rosieapp.services.common.model.construction.MapBasedModelBuilderTest."
              + "TestModelBuilder{"
                + "\"id\": \"null\", "
                + "\"field1\": \"value1\""
              + "}");
        });
      });

      context("when there are multiple fields", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(() -> {
          final TestModelBuilder result = new TestModelBuilder();

          result.putFieldValue("field1", "value1");
          result.putFieldValue("field2", "value2");

          return result;
        });

        it("returns `ClassName{\"id\": \"null\", \"field1\": \"value1\", "
           + "\"field2\":\"value2\"}`", () -> {
          assertThat(modelBuilder.get().toString())
            .isEqualTo(
              "com.rosieapp.services.common.model.construction.MapBasedModelBuilderTest."
              + "TestModelBuilder{"
                + "\"id\": \"null\", "
                + "\"field1\": \"value1\", "
                + "\"field2\": \"value2\""
              + "}");
        });
      });
    });

    describe("#putFieldValue()", () -> {
      context("when the value is not already in the map", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(TestModelBuilder::new);

        it("adds the value to the map", () -> {
          final TestModelBuilder builder = modelBuilder.get();

          builder.putFieldValue("field", "value");

          assertThat((String)builder.getFieldValue("field")).isEqualTo("value");
        });
      });

      context("when the value is already in the map", () -> {
        final Supplier<TestModelBuilder> modelBuilder = let(() -> {
          final TestModelBuilder result = new TestModelBuilder();

          result.putFieldValue("field", "value1");

          return result;
        });

        it("replaces the existing value", () -> {
          final TestModelBuilder builder = modelBuilder.get();

          builder.putFieldValue("field", "value2");

          assertThat((String)builder.getFieldValue("field")).isEqualTo("value2");
        });
      });
    });
  }

  private static class TestModel
  extends AbstractModel {
    public String requiredField;
    public String optionalField;
  }

  private static class TestModelBuilder
  extends MapBasedModelBuilder<TestModel, TestModelBuilder> {
    public TestModelBuilder() {
      super();
    }

    public TestModelBuilder(final FieldDependencyHandler valueProvider) {
      super(valueProvider);
    }

    @Override
    public <F> void putFieldValue(final String fieldName, final F value) {
      super.putFieldValue(fieldName, value);
    }

    @Override
    public <F> F getFieldValue(final String fieldName) {
      return super.getFieldValue(fieldName);
    }

    public TestModelBuilder withRequiredField(final String requiredField) {
      this.putFieldValue("requiredField", requiredField);

      return this;
    }

    public TestModelBuilder withOptionalField(final String optionalField) {
      this.putFieldValue("optionalField", optionalField);

      return this;
    }

    @Override
    public TestModel build() {
      final TestModel model = new TestModel();

      model.assignId(super.buildId());

      model.requiredField = this.getRequiredFieldValue("requiredField");
      model.optionalField = this.getOptionalFieldValue("optionalField", "Default");

      return model;
    }

    @Override
    public TestModel buildShallow() {
      // Unused by this test
      return null;
    }

    @Override
    public ModelFilterBuilder<TestModel> toFilterBuilder() {
      // Unused by this test
      return null;
    }
  }
}
