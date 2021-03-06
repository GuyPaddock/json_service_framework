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
import static com.greghaskins.spectrum.dsl.specification.Specification.eagerLet;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.services.common.model.fieldhandling.FieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.FieldValuePreprocessor;
import com.rosieapp.services.common.model.fieldhandling.PassthroughPreprocessor;
import com.rosieapp.services.common.model.fieldhandling.RequiredFieldMissingException;
import com.rosieapp.services.common.model.filtering.ModelFilter;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.ModelIdentifierFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
public class AnnotationBasedModelBuilderTest {
  {
    describe("constructors", () -> {
      context("when no field dependency handler is provided", () -> {
        final Supplier<ModelWithOnlyBuilderFields.Builder> modelBuilder =
          let(() -> ModelWithOnlyBuilderFields.getBuilder());

        it("is initialized to strictly validate required fields", () -> {
          assertThatExceptionOfType(RequiredFieldMissingException.class)
            .isThrownBy(() -> {
              modelBuilder.get().build();
            });
        });

        it("is initialized not to throw an exception for missing optional fields", () -> {
          assertThatCode(() -> {
            modelBuilder.get().withRequiredCast("satisfied").build();
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
              new ModelWithOnlyBuilderFields.Builder(dependencyHandler.get())
                .withRequiredCast("someRequiredValue")
                .withOptionalCast("someOptionalValue");

            return result;
          });

        it("uses the provided FDH to validate required fields", () -> {
          modelBuilder.get().build();

          verify(dependencyHandler.get())
            .handleRequiredField("someRequiredValue", "requiredCast");
        });

        it("uses the provided FDH to validate optional fields", () -> {
          modelBuilder.get().build();

          verify(dependencyHandler.get())
            .handleOptionalField("someOptionalValue", "optionalCast", null);
        });
      });
    });

    describe("#build()", () -> {
      describe("field population", () -> {
        context("when the model has no fields", () -> {
          final Supplier<Model> model = let(() -> ModelWithNoFields.getBuilder().build());

          it("builds the model without having to populate any fields", () -> {
            assertThat(model.get()).isInstanceOf(ModelWithNoFields.class);
          });
        });

        context("when the model has fields but none are builder-populated", () -> {
          final Supplier<ModelWithNoBuilderFields> model =
            let(() -> ModelWithNoBuilderFields.getBuilder().build());

          it("builds the model without populating any fields", () -> {
            assertThat(model.get().nonBuilderFields).isEqualTo("some value");
          });
        });

        context("when the model has only fields that are builder-populated", () -> {
          final Supplier<ModelWithOnlyBuilderFields> model = let(() -> {
            final ModelWithOnlyBuilderFields result;

            result =
              ModelWithOnlyBuilderFields
                .getBuilder()
                .withRequiredCast("Sterling Archer")
                .withOptionalCast("Malory Archer")
                .build();

            return result;
          });

          it("builds the model and populates all fields", () -> {
            assertThat(model.get().requiredCast).isEqualTo("Sterling Archer");
            assertThat(model.get().optionalCast).isEqualTo("Malory Archer");
          });
        });

        context("when the model has some builder-populated fields and some regular fields", () -> {
          final Supplier<ModelWithBuilderAndNonBuilderFields> model = let(() -> {
            final ModelWithBuilderAndNonBuilderFields result;

            result =
              ModelWithBuilderAndNonBuilderFields
                .getBuilder()
                .withRequiredCast("Sterling Archer")
                .withOptionalCast("Malory Archer")
                .build();

            return result;
          });

          it("builds the model and populates just the builder-populated fields", () -> {
            assertThat(model.get().requiredCast).isEqualTo("Sterling Archer");
            assertThat(model.get().optionalCast).isEqualTo("Malory Archer");
            assertThat(model.get().internalSupportingCast).isEqualTo("Woodhouse");
          });
        });

        context("when the model extends another model and both have builder-populated "
                + "fields", () -> {
          final Supplier<ModelThatExtendsAnotherModel> model = let(() -> {
            final ModelThatExtendsAnotherModel result;

            result =
              ModelThatExtendsAnotherModel
                .getBuilder()
                .withRequiredCast("Sterling Archer")
                .withOptionalCast("Malory Archer")
                .withAdditionalCast("Cyril Figgis")
                .build();

            return result;
          });

          it("builds the model and populates builder-populated fields from both classes", () -> {
            assertThat(model.get().requiredCast).isEqualTo("Sterling Archer");
            assertThat(model.get().optionalCast).isEqualTo("Malory Archer");
            assertThat(model.get().additionalCast).isEqualTo("Cyril Figgis");
          });

          it("ensures that required fields in the parent class are filled out", () -> {
            assertThatExceptionOfType(RequiredFieldMissingException.class).isThrownBy(() -> {
              ModelThatExtendsAnotherModel
                .getBuilder()
                .withOptionalCast("Malory Archer")
                .withAdditionalCast("Cyril Figgis")
                .build();
            })
            .withMessage(
              "`requiredCast` is a required field that has not been provided with a value")
            .withNoCause();
          });

          it("ensures that required fields in the sub-class are filled out", () -> {
            assertThatExceptionOfType(RequiredFieldMissingException.class).isThrownBy(() -> {
              ModelThatExtendsAnotherModel
                .getBuilder()
                .withRequiredCast("Sterling Archer")
                .withOptionalCast("Malory Archer")
                .build();
            }).withMessage(
              "`additionalCast` is a required field that has not been provided with a value");
          });
        });

        context("when a builder has the wrong data type for a field it populates", () -> {
          final Supplier<Throwable> expectedCause = let(() -> {
            return new IllegalArgumentException(
              "Can not set java.lang.String field com.rosieapp.services.common.model.construction."
              + "AnnotationBasedModelBuilderTest$ModelWithMisconfiguredBuilder.stringField to "
              + "java.lang.Boolean");
          });

          it("throws an `IllegalStateException`", () -> {
            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
              ModelWithMisconfiguredBuilder
                .getBuilder()
                .withStringField(false)
                .build();
            })
            .withMessage(
              "Could not populate the field `stringField` on model type "
              + "`com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilderTest"
              + ".ModelWithMisconfiguredBuilder`")
            .withCause(expectedCause.get());
          });
        });

        context("when a builder attempts to populate a field that is not builder-populated", () -> {
          it("throws an `IllegalArgumentException`", () -> {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
              ModelWithMisconfiguredBuilder
                .getBuilder()
                .withNonBuilderPopulatedField("test")
                .build();
            })
            .withMessage(
              "No field within "
              + "`com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilderTest"
              + ".ModelWithMisconfiguredBuilder` named `nonBuilderPopulatedField` and annotated "
              + "with BuilderPopulatedField was found.")
            .withNoCause();
          });
        });

        context("when a builder attempts to populate a field that does not exist", () -> {
          it("throws an `IllegalArgumentException`", () -> {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
              ModelWithMisconfiguredBuilder
                .getBuilder()
                .withNonExistentField("test")
                .build();
            })
            .withMessage(
              "No field within "
              + "`com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilderTest"
              + ".ModelWithMisconfiguredBuilder` named `nonExistentField` and annotated with "
              + "BuilderPopulatedField was found.")
            .withNoCause();
          });
        });
      });

      describe("field pre-processing", () -> {
        final Supplier<List<String>> copiedList = let(() -> Arrays.asList("a", "b", "c"));

        final Supplier<List<String>> passthroughList = let(() -> Arrays.asList("1", "2", "3"));

        final Supplier<ModelWithFieldPreprocessors.Builder> builder = let(() -> {
          final ModelWithFieldPreprocessors.Builder result;

          result =
            ModelWithFieldPreprocessors
              .getBuilder()
              .withCopiedField(copiedList.get())
              .withPassedThroughField(passthroughList.get());

          return result;
        });

        final Supplier<ModelWithFieldPreprocessors> model1 = eagerLet(() -> builder.get().build());

        final Supplier<ModelWithFieldPreprocessors> model2 = eagerLet(() -> builder.get().build());

        context("when a field uses the default pre-processor (`CloningPreprocessor`)", () -> {
          it("populates that field in the new model with a copy of the value from the builder "
             + "context", () -> {
            assertThat(model1.get().copiedField).isEqualTo(copiedList.get());
            assertThat(model1.get().copiedField).isNotSameAs(copiedList.get());

            assertThat(model2.get().copiedField).isEqualTo(copiedList.get());
            assertThat(model2.get().copiedField).isNotSameAs(copiedList.get());
          });

          it("uses a different copy for each model instance if the same builder is used to produce "
             + "multiple model instances", () -> {
            assertThat(model1.get().copiedField).isNotSameAs(model2.get().copiedField);
          });
        });

        context("when a field uses the `PassthroughPreprocessor", () -> {
          it("populates that field in the new model with the same value contained in the builder "
             + "context", () -> {
            assertThat(model1.get().passedThroughField).isSameAs(passthroughList.get());
            assertThat(model2.get().passedThroughField).isSameAs(passthroughList.get());
          });

          it("populates every new instance with the same value if the same builder is used to "
             + "produce multiple model instances", () -> {
            assertThat(model1.get().passedThroughField).isSameAs(model2.get().passedThroughField);
          });
        });

        context("when a builder has a bad field pre-processor", () -> {
          final Supplier<Throwable> expectedCause = let(() -> {
            return new IllegalArgumentException(
              "Invalid field pre-processor provided -- `com.rosieapp.services.common.model"
              + ".fieldhandling.FieldValuePreprocessor` cannot be instantiated.");
          });

          it("throws an `IllegalStateException`", () -> {
            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
              ModelWithMisconfiguredBuilder
                .getBuilder()
                .withFieldThatHasBadPreprocessor("test")
                .build();
            })
            .withMessage(
              "Could not populate the field `fieldWithBadPreprocessor` on model type "
              + "`com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilderTest."
              + "ModelWithMisconfiguredBuilder`")
            .withCause(expectedCause.get());
          });
        });
      });
    });

    describe("#buildShallow()", () -> {
      final Supplier<ModelIdentifier> sampleId = let(() -> ModelIdentifierFactory.valueOf("ant1"));

      context("when an ID has not been provided", () -> {
        it("throws an `IllegalStateException` exception", () -> {
          assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            ModelThatExtendsAnotherModel.getBuilder().buildShallow();
          })
          .withMessage("`id` must be set prior to calling this method")
          .withNoCause();
        });
      });

      context("when only an ID has not been provided", () -> {
        it("builds a model with the ID populated", () -> {
          final ModelThatExtendsAnotherModel shallowModel;

          shallowModel =
            ModelThatExtendsAnotherModel
              .getBuilder()
              .withId(sampleId.get())
              .buildShallow();

          assertThat(shallowModel.getId()).isEqualTo(sampleId.get());
        });
      });

      context("when an ID and other fields have been provided", () -> {
        it("builds a model where only the ID is populated", () -> {
          final ModelThatExtendsAnotherModel shallowModel;

          shallowModel =
            ModelThatExtendsAnotherModel
              .getBuilder()
              .withId(sampleId.get())
              .withRequiredCast("Sterling Archer")
              .withOptionalCast("Mallory Archer")
              .withAdditionalCast("Woodhouse")
              .buildShallow();

          assertThat(shallowModel.getId()).isEqualTo(sampleId.get());
          assertThat(shallowModel.requiredCast).isNull();
          assertThat(shallowModel.optionalCast).isNull();
          assertThat(shallowModel.additionalCast).isNull();
        });
      });
    });

    describe("#toFilterBuilder()", () -> {
      final Supplier<ModelThatExtendsAnotherModel> shallowModel1 =
        let(() -> ModelThatExtendsAnotherModel.getBuilder().withId("shallowModel1").buildShallow());

      final Supplier<ModelThatExtendsAnotherModel> shallowModel2 =
        let(() -> ModelThatExtendsAnotherModel.getBuilder().withId("shallowModel2").buildShallow());

      final Supplier<ModelThatExtendsAnotherModel> populatedModel = let(() -> {
        final ModelThatExtendsAnotherModel model;

        model =
          ModelThatExtendsAnotherModel
            .getBuilder()
            .withId("populatedModel")
            .withRequiredCast("Sterling Archer")
            .withAdditionalCast("Woodhouse")
            .build();

        return model;
      });

      context("when the model builder has no context", () -> {
        final Supplier<ModelThatExtendsAnotherModel.Builder> builder =
          let(() -> ModelThatExtendsAnotherModel.getBuilder());

        it("constructs a filter builder with an empty context", () -> {
          final ModelFilter<ModelThatExtendsAnotherModel> filter;

          filter = builder.get().toFilterBuilder().build();

          assertThat(filter.matches(shallowModel1.get())).isTrue();
          assertThat(filter.matches(shallowModel2.get())).isTrue();
          assertThat(filter.matches(populatedModel.get())).isTrue();
        });
      });

      context("when the model builder has an ID in the context", () -> {
        final Supplier<List<ModelThatExtendsAnotherModel.Builder>> builders = let(() -> {
          return Arrays.asList(
            ModelThatExtendsAnotherModel
              .getBuilder()
              .withId("shallowModel1"),

            ModelThatExtendsAnotherModel
              .getBuilder()
              .withId("shallowModel2"),

            ModelThatExtendsAnotherModel
              .getBuilder()
              .withId("populatedModel")
          );
        });

        final Supplier<List<ModelFilter<ModelThatExtendsAnotherModel>>> filters = let(() -> {
          return builders
            .get()
            .stream()
            .map((builder) -> builder.toFilterBuilder().build())
            .collect(Collectors.toList());
        });

        it("constructs a filter builder that has ID equality in its context", () -> {
          final List<ModelFilter<ModelThatExtendsAnotherModel>> localFilters = filters.get();

          assertThat(localFilters.get(0).matches(shallowModel1.get())).isTrue();
          assertThat(localFilters.get(0).matches(shallowModel2.get())).isFalse();
          assertThat(localFilters.get(0).matches(populatedModel.get())).isFalse();

          assertThat(localFilters.get(1).matches(shallowModel1.get())).isFalse();
          assertThat(localFilters.get(1).matches(shallowModel2.get())).isTrue();
          assertThat(localFilters.get(1).matches(populatedModel.get())).isFalse();

          assertThat(localFilters.get(2).matches(shallowModel1.get())).isFalse();
          assertThat(localFilters.get(2).matches(shallowModel2.get())).isFalse();
          assertThat(localFilters.get(2).matches(populatedModel.get())).isTrue();
        });
      });

      context("when the model builder has a model field in its context", () -> {
        final Supplier<ModelThatExtendsAnotherModel.Builder> builder = let(() -> {
          final ModelThatExtendsAnotherModel.Builder result;

          result =
            ModelThatExtendsAnotherModel
              .getBuilder()
              .withAdditionalCast("Woodhouse");

          return result;
        });

        it("constructs a filter builder that has equality for that field in its context", () -> {
          final ModelFilter<ModelThatExtendsAnotherModel> filter;

          filter = builder.get().toFilterBuilder().build();

          assertThat(filter.matches(shallowModel1.get())).isFalse();
          assertThat(filter.matches(shallowModel2.get())).isFalse();
          assertThat(filter.matches(populatedModel.get())).isTrue();
        });
      });
    });
  }

  public static class ModelWithNoFields
  extends AbstractModel {
    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithNoFields() {
      super();
    }

    public static class Builder
    extends AnnotationBasedModelBuilder<ModelWithNoFields, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }
    }
  }

  public static class ModelWithNoBuilderFields
  extends AbstractModel {
    public String nonBuilderFields;

    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithNoBuilderFields() {
      super();

      this.nonBuilderFields = "some value";
    }

    public static class Builder
    extends AnnotationBasedModelBuilder<ModelWithNoBuilderFields, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }
    }
  }

  public static class ModelWithOnlyBuilderFields
  extends AbstractModel {
    @BuilderPopulatedField(required = true)
    public String requiredCast;

    @BuilderPopulatedField
    public String optionalCast;

    @SuppressWarnings({
      "unchecked" // Class invariants guarantee run-time type safety of generic cast
    })
    public static <M extends ModelWithOnlyBuilderFields, B extends Builder<M, ?>> B getBuilder() {
      return (B)new Builder<>();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithOnlyBuilderFields() {
      super();
    }

    public static class Builder<M extends ModelWithOnlyBuilderFields, B extends Builder<M, B>>
    extends AnnotationBasedModelBuilder<M, Builder<M, B>> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }

      @SuppressWarnings({
        "unchecked" // Class invariants guarantee run-time type safety of generic cast
      })
      @Override
      public B withId(final String identifier)
      throws IllegalArgumentException, NullPointerException {
        return (B)super.withId(identifier);
      }

      @SuppressWarnings({
        "unchecked" // Class invariants guarantee run-time type safety of generic cast
      })
      @Override
      public B withId(final ModelIdentifier identifier) {
        return (B)super.withId(identifier);
      }

      @SuppressWarnings({
        "unchecked", // Class invariants guarantee run-time type safety of generic cast
        "CheckStyle" // No javadoc required for test class
      })
      public B withRequiredCast(final String requiredCast) {
        this.putFieldValue("requiredCast", requiredCast);

        return (B)this;
      }

      @SuppressWarnings({
        "unchecked", // Class invariants guarantee run-time type safety of generic cast
        "CheckStyle" // No javadoc required for test class
      })
      public B withOptionalCast(final String optionalCast) {
        this.putFieldValue("optionalCast", optionalCast);

        return (B)this;
      }
    }
  }

  public static class ModelThatExtendsAnotherModel
  extends ModelWithOnlyBuilderFields {
    @BuilderPopulatedField(required = true)
    public String additionalCast;

    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelThatExtendsAnotherModel() {
      super();
    }

    public static class Builder
    extends ModelWithOnlyBuilderFields.Builder<ModelThatExtendsAnotherModel, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }

      @SuppressWarnings("CheckStyle")
      public Builder withAdditionalCast(final String additionalCast) {
        this.putFieldValue("additionalCast", additionalCast);

        return this;
      }
    }
  }

  public static class ModelWithBuilderAndNonBuilderFields
  extends AbstractModel {
    @BuilderPopulatedField(required = true)
    public String requiredCast;

    @BuilderPopulatedField
    public String optionalCast;

    public String internalSupportingCast;

    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithBuilderAndNonBuilderFields() {
      super();

      this.internalSupportingCast = "Woodhouse";
    }

    public static class Builder
    extends AnnotationBasedModelBuilder<ModelWithBuilderAndNonBuilderFields, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }

      @SuppressWarnings("CheckStyle")
      public Builder withRequiredCast(final String requiredCast) {
        this.putFieldValue("requiredCast", requiredCast);

        return this;
      }

      @SuppressWarnings("CheckStyle")
      public Builder withOptionalCast(final String optionalCast) {
        this.putFieldValue("optionalCast", optionalCast);

        return this;
      }
    }
  }

  @SuppressWarnings("unused")
  public static class ModelWithFieldPreprocessors
  extends AbstractModel {
    @BuilderPopulatedField(required = true)
    public List<String> copiedField;

    @BuilderPopulatedField(required = true, preprocessor = PassthroughPreprocessor.class)
    public List<String> passedThroughField;

    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithFieldPreprocessors() {
      super();
    }

    public static class Builder
      extends AnnotationBasedModelBuilder<ModelWithFieldPreprocessors, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }

      @SuppressWarnings("CheckStyle")
      public Builder withCopiedField(final List<String> copiedField) {
        this.putFieldValue("copiedField", copiedField);

        return this;
      }

      @SuppressWarnings("CheckStyle")
      public Builder withPassedThroughField(final List<String> passedThroughField) {
        this.putFieldValue("passedThroughField", passedThroughField);

        return this;
      }
    }
  }

  @SuppressWarnings("unused")
  public static class ModelWithMisconfiguredBuilder
  extends AbstractModel {
    @BuilderPopulatedField(preprocessor = FieldValuePreprocessor.class)
    private String fieldWithBadPreprocessor;

    @BuilderPopulatedField(required = true)
    private String stringField;

    private String nonBuilderPopulatedField;

    public static Builder getBuilder() {
      return new Builder();
    }

    /**
     * Private constructor for builder-instantiated model.
     */
    private ModelWithMisconfiguredBuilder() {
      super();
    }

    public static class Builder
    extends AnnotationBasedModelBuilder<ModelWithMisconfiguredBuilder, Builder> {
      public Builder() {
        super();
      }

      public Builder(final FieldDependencyHandler valueProvider) {
        super(valueProvider);
      }


      @SuppressWarnings("CheckStyle")
      public Builder withFieldThatHasBadPreprocessor(final String value) {
        this.putFieldValue("fieldWithBadPreprocessor", value);

        return this;
      }

      @SuppressWarnings("CheckStyle")
      public Builder withStringField(final Boolean value) {
        this.putFieldValue("stringField", value);

        return this;
      }

      @SuppressWarnings("CheckStyle")
      public Builder withNonBuilderPopulatedField(final String value) {
        this.putFieldValue("nonBuilderPopulatedField", value);

        return this;
      }

      @SuppressWarnings("CheckStyle")
      public Builder withNonExistentField(final String value) {
        this.putFieldValue("nonExistentField", value);

        return this;
      }
    }
  }
}
