/*
 * Copyright (c) 2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.construction;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.fieldhandling.FieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.LaxFieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.RequiredFieldMissingException;
import com.rosieapp.services.common.model.filtering.ModelFilterBuilder;
import com.rosieapp.services.common.model.identification.LongIdentifier;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.StringIdentifier;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Spectrum.class)
@PrepareForTest({ StringIdentifier.class })
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr"
})
public class AbstractModelBuilderTest {
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

    describe("#withId(String)", () -> {
      final Supplier<TestModelBuilder> modelBuilder =
        let(() -> new TestModelBuilder(new LaxFieldDependencyHandler()));

      context("when the provided identifier is `null`", () -> {
        it("throws `NullPointerException`", () -> {
          assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> {
              modelBuilder.get().withId((String)null);
            })
            .withMessage("identifier cannot be null");
        });

        it("does not impact what identifier builds subsequent models", () -> {
          final TestModelBuilder  builder = modelBuilder.get();
          final ModelIdentifier   builtId;

          try {
            builder.withId((String)null);
          } catch (NullPointerException ex) {
            // Silence exception
          }

          builtId = builder.build().getId();

          assertThat(builtId).isNotNull();
          assertThat(builtId.isObjectNew()).isTrue();
        });
      });

      context("when the provided identifier is not a valid identifier", () -> {
        beforeEach(() -> {
          mockStatic(StringIdentifier.class);

          when(StringIdentifier.createFrom("invalid")).thenReturn(Optional.empty());
        });

        it("throws `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              modelBuilder.get().withId("invalid");
            })
            .withMessage("Unrecognized identifier format: invalid");
        });

        it("does not impact what identifier builds subsequent models", () -> {
          final TestModelBuilder  builder = modelBuilder.get();
          final ModelIdentifier   builtId;

          try {
            builder.withId("invalid");
          } catch (IllegalArgumentException ex) {
            // Silence exception
          }

          builtId = builder.build().getId();

          assertThat(builtId).isNotEqualTo(new StringIdentifier("invalid"));
          assertThat(builtId.isObjectNew()).isTrue();
        });
      });

      context("when the provided identifier is a valid identifier", () -> {
        it("does not throw any exceptions", () -> {
          assertThatCode(() -> {
            modelBuilder.get().withId("8675309");
          }).doesNotThrowAnyException();
        });

        it("builds a model with the provided identifier", () -> {
          final TestModelBuilder builder = modelBuilder.get();

          builder.withId("8675309");

          assertThat(builder.build().getId())
            .isEqualTo(new LongIdentifier(8675309));
        });
      });
    });

    describe("#withId(ModelIdentifier)", () -> {
      final Supplier<TestModelBuilder> modelBuilder =
        let(() -> new TestModelBuilder(new LaxFieldDependencyHandler()));

      context("when the provided identifier is `null`", () -> {
        it("throws `NullPointerException`", () -> {
          assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> {
              modelBuilder.get().withId((ModelIdentifier) null);
            })
            .withMessage("identifier cannot be null");
        });

        it("does not impact what identifier builds subsequent models", () -> {
          final TestModelBuilder  builder = modelBuilder.get();
          final ModelIdentifier   builtId;

          try {
            builder.withId((ModelIdentifier)null);
          } catch (NullPointerException ex) {
            // Silence exception
          }

          builtId = builder.build().getId();

          assertThat(builtId).isNotNull();
          assertThat(builtId.isObjectNew()).isTrue();
        });
      });

      context("when the provided identifier is not null", () -> {
        final Supplier<ModelIdentifier> identifier = let(() -> new LongIdentifier(8675309));

        it("does not throw any exceptions", () -> {
          assertThatCode(() -> {
            modelBuilder.get().withId(identifier.get());
          }).doesNotThrowAnyException();
        });

        it("builds a model with the provided identifier", () -> {
          final TestModelBuilder builder = modelBuilder.get();

          builder.withId(identifier.get());

          assertThat(builder.build().getId()).isEqualTo(identifier.get());
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
  extends AbstractModelBuilder<TestModel, TestModelBuilder> {
    private String requiredField;
    private String optionalField;

    public TestModelBuilder() {
      super();
    }

    public TestModelBuilder(final FieldDependencyHandler valueProvider) {
      super(valueProvider);
    }

    public TestModelBuilder withRequiredField(final String requiredField) {
      this.requiredField = requiredField;

      return this;
    }

    public TestModelBuilder withOptionalField(final String optionalField) {
      this.optionalField = optionalField;

      return this;
    }

    @Override
    public TestModel build() {
      final TestModel model = new TestModel();

      model.assignId(super.buildId());

      model.requiredField =
        this.supplyRequiredFieldValue(this.requiredField, "requiredField");

      model.optionalField =
        this.supplyOptionalFieldValue(this.optionalField, "optionalField", "Default");

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
