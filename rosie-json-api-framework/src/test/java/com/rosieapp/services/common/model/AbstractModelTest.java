/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.identification.LongIdentifier;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.NewModelIdentifier;
import com.rosieapp.services.common.model.identification.StringIdentifier;
import com.rosieapp.services.common.model.tests.JSONTestHelper;
import com.rosieapp.util.test.JSONUtils;
import java.util.function.Supplier;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "PMD.AvoidDuplicateLiterals",
  "PMD.NcssCount",
  "PMD.NonStaticInitializer",
  "PMD.TooManyStaticImports"
})
public class AbstractModelTest {
  {
    beforeAll(JSONTestHelper::configureTestForJackson);

    describe("#assignId", () -> {
      context("when the model does not already have an ID assigned", () -> {
        final Supplier<Model> model = let(TestModel::new);
        final Supplier<ModelIdentifier> newId = let(() -> new LongIdentifier(8675309));

        beforeEach(() -> {
          model.get().assignId(newId.get());
        });

        it("assigns the model the provided, new ID", () -> {
          assertThat(model.get().getId()).isSameAs(newId.get());
        });
      });

      context("when the model has a placeholder ID assigned", () -> {
        final Supplier<Model> model = let(() -> {
          final Model result = new TestModel();

          result.assignId(NewModelIdentifier.getInstance());

          return result;
        });

        context("when given a null ID", () -> {
          final Supplier<ModelIdentifier> newId = let(() -> null);

          it("throws a NullPointerException", () -> {
            assertThatExceptionOfType(NullPointerException.class)
              .isThrownBy(() -> {
                model.get().assignId(newId.get());
              })
              .withMessage("`newId` cannot be null")
              .withNoCause();
          });

          it("does not change the ID of the model", () -> {
            try {
              model.get().assignId(newId.get());
            }
            catch (Exception ex) {
              // Silence exception
            }

            assertThat(model.get().getId()).isSameAs(NewModelIdentifier.getInstance());
          });
        });

        context("when given a non-null ID", () -> {
          final Supplier<ModelIdentifier> newId = let(() -> new LongIdentifier(8675309));

          beforeEach(() -> {
            model.get().assignId(newId.get());
          });

          it("assigns the model the provided, new ID", () -> {
            assertThat(model.get().getId()).isSameAs(newId.get());
          });
        });
      });

      context("when the model has been assigned an ID that is not a placeholder", () -> {
        final Supplier<ModelIdentifier> originalId = let(() -> new LongIdentifier(8675309));

        final Supplier<Model> model =
          let(() -> {
            final Model result = new TestModel();

            result.assignId(originalId.get());

            return result;
          });

        context("when the ID being assigned is the same as the model's current ID", () -> {
          final Supplier<ModelIdentifier> newId = let(() -> new LongIdentifier(8675309));

          beforeEach(() -> {
            model.get().assignId(newId.get());
          });

          it("does not make any changes to the model's ID", () -> {
            assertThat(model.get().getId()).isSameAs(originalId.get());
          });
        });

        context("when the ID being assigned is different than the model's current ID", () -> {
          context("when given a null ID", () -> {
            final Supplier<ModelIdentifier> newId = let(() -> null);

            it("throws a NullPointerException", () -> {
              assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> {
                  model.get().assignId(newId.get());
                })
                .withMessage("`newId` cannot be null")
                .withNoCause();
            });

            it("does not change the ID of the model", () -> {
              try {
                model.get().assignId(newId.get());
              }
              catch (Exception ex) {
                // Silence exception
              }

              assertThat(model.get().getId()).isSameAs(originalId.get());
            });
          });

          context("when given a non-null ID", () -> {
            final Supplier<ModelIdentifier> newId = let(() -> new LongIdentifier(123456));

            it("throws an IllegalStateException", () -> {
              assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> {
                  model.get().assignId(newId.get());
                })
                .withMessage(
                  "This model already has an existing identifier set. An attempt was made to " +
                  "change the identifier from `8675309` to `123456`")
                .withNoCause();
            });

            it("does not make any changes to the model's ID", () -> {
              try {
                model.get().assignId(newId.get());
              }
              catch (Exception ex) {
                // Silence exception
              }

              assertThat(model.get().getId()).isSameAs(originalId.get());
            });
          });

          context("when given a placeholder ID", () -> {
            final Supplier<ModelIdentifier> newId = let(NewModelIdentifier::getInstance);

            it("throws an IllegalStateException", () -> {
              assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> {
                  model.get().assignId(newId.get());
                })
                .withMessage(
                  "This model already has an existing identifier set. An attempt was made to " +
                  "change the identifier from `8675309` to `null`")
                .withNoCause();
            });

            it("does not make any changes to the model's ID", () -> {
              try {
                model.get().assignId(newId.get());
              }
              catch (Exception ex) {
                // Silence exception
              }

              assertThat(model.get().getId()).isSameAs(originalId.get());
            });
          });
        });
      });
    });

    describe("#getId", () -> {
      context("when the model does not already have an ID assigned", () -> {
        final Supplier<Model> model = let(TestModel::new);

        it("returns an ID that responds to `isObjectNew()` with `true`", () -> {
          assertThat(model.get().getId().isObjectNew()).isTrue();
        });
      });

      context("when the model has a placeholder ID assigned", () -> {
        final Supplier<Model> model = let(() -> {
          final Model result = new TestModel();

          result.assignId(NewModelIdentifier.getInstance());

          return result;
        });

        it("returns an ID that responds to `isObjectNew()` with `true`", () -> {
          assertThat(model.get().getId().isObjectNew()).isTrue();
        });
      });

      context("when the model has an ID assigned that is not a placeholder", () -> {
        final Supplier<ModelIdentifier> identifier = let(() -> new StringIdentifier("yabba-dabba-doo"));

        final Supplier<Model> model = let(() -> {
          final Model result = new TestModel();

          result.assignId(identifier.get());

          return result;
        });

        it("returns the same instance of the ID that was provided to assignId()", () -> {
          assertThat(model.get().getId()).isSameAs(identifier.get());
        });
      });
    });

    describe("#isNew", () -> {
      context("when the model does not already have an ID assigned", () -> {
        final Supplier<Model> model = let(TestModel::new);

        it("returns `true`", () -> {
          assertThat(model.get().isNew()).isTrue();
        });
      });

      context("when the model has a placeholder ID assigned", () -> {
        final Supplier<Model> model = let(() -> {
          final Model result = new TestModel();

          result.assignId(NewModelIdentifier.getInstance());

          return result;
        });

        it("returns `true`", () -> {
          assertThat(model.get().isNew()).isTrue();
        });
      });

      context("when the model has an ID assigned that is not a placeholder", () -> {
        final Supplier<ModelIdentifier> identifier = let(() -> new StringIdentifier("yabba-dabba-doo"));

        final Supplier<Model> model = let(() -> {
          final Model result = new TestModel();

          result.assignId(identifier.get());

          return result;
        });

        it("returns `false`", () -> {
          assertThat(model.get().isNew()).isFalse();
        });
      });
    });

    describe("#equals", () -> {
      final Supplier<Model> model = let(TestModel::new);

      context("when given the same object", () -> {
        final Supplier<Object> otherObject = let(model::get);

        it("returns `true`", () -> {
          assertThat(model.get().equals(otherObject.get())).isTrue();
        });
      });

      context("when given null", () -> {
        final Supplier<Object> otherObject = let(() -> null);

        it("returns `false`", () -> {
          assertThat(model.get().equals(otherObject.get())).isFalse();
        });
      });

      context("when given an object of a different type", () -> {
        final Supplier<Object> otherObject = let(() -> "Hi!");

        it("returns `false`", () -> {
          assertThat(model.get().equals(otherObject.get())).isFalse();
        });
      });

      context("when the model is new", () -> {
        context("when given an object that is not new", () -> {
          final Supplier<Object> otherObject = let(() -> {
            final Model otherModel = new TestModel();

            otherModel.assignId(new LongIdentifier(12345));

            return otherModel;
          });

          it("returns `false`", () -> {
            assertThat(model.get().equals(otherObject.get())).isFalse();
          });
        });

        context("when given a different object that is new", () -> {
          final Supplier<Object> otherObject = let(TestModel::new);

          it("returns `false`", () -> {
            assertThat(model.get().equals(otherObject.get())).isFalse();
          });
        });
      });

      context("when the model is not new", () -> {
        beforeEach(() -> {
          model.get().assignId(new StringIdentifier("Model A"));
        });

        context("when given an object with the same ID", () -> {
          final Supplier<Object> otherObject = let(() -> {
            final Model otherModel = new TestModel();

            otherModel.assignId(new StringIdentifier("Model A"));

            return otherModel;
          });

          it("returns `true`", () -> {
            assertThat(model.get().equals(otherObject.get())).isTrue();
          });
        });

        context("when given an object with a different ID", () -> {
          final Supplier<Object> otherObject = let(() -> {
            final Model otherModel = new TestModel();

            otherModel.assignId(new StringIdentifier("Model B"));

            return otherModel;
          });

          it("returns `false`", () -> {
            assertThat(model.get().equals(otherObject.get())).isFalse();
          });
        });

        context("when given an object that is new", () -> {
          final Supplier<Object> otherObject = let(TestModel::new);

          it("returns `false`", () -> {
            assertThat(model.get().equals(otherObject.get())).isFalse();
          });
        });
      });
    });

    describe("#hashCode", () -> {
      context("when the model is new", () -> {
        final Supplier<Model> model1 = let(TestModel::new);

        final Supplier<Model> model2 = let(TestModel::new);

        it("returns a unique hashcode for each instance", () -> {
          assertThat(model1.get().hashCode()).isNotEqualTo(model2.get().hashCode());
        });
      });

      context("when the model is not new", () -> {
        final Supplier<Model> model1 = let(() -> {
          final Model model = new TestModel();

          model.assignId(new StringIdentifier("Model 1"));

          return model;
        });

        final Supplier<Model> model2 = let(() -> {
          final Model model = new TestModel();

          model.assignId(new StringIdentifier("Model 1"));

          return model;
        });

        final Supplier<Model> model3 = let(() -> {
          final Model model = new TestModel();

          model.assignId(new StringIdentifier("Model 2"));

          return model;
        });

        it("returns the same hashcode for each instance with the same ID", () -> {
          assertThat(model1.get().hashCode()).isEqualTo(model2.get().hashCode());
        });

        it("returns a different hashcode for instances with different IDs", () -> {
          assertThat(model1.get().hashCode()).isNotEqualTo(model3.get().hashCode());
          assertThat(model2.get().hashCode()).isNotEqualTo(model3.get().hashCode());
        });
      });
    });

    describe("#clone", () -> {
      final Supplier<TestModel> model = let(() -> {
        final TestModel result = new TestModel();

        result.testField = "abc123";

        return result;
      });

      context("when the model is new", () -> {
        final Supplier<Object> clone = let(() -> model.get().clone());

        it("creates a distinct instance of the same type", () -> {
          assertThat(clone.get()).isExactlyInstanceOf(TestModel.class);
          assertThat(clone.get()).isNotSameAs(model.get());
        });

        it("performs a shallow copy of the state of the model", () -> {
          assertThat(((TestModel)clone.get()).testField).isEqualTo("abc123");
        });
      });

      context("when the model is not new", () -> {
        final Supplier<ModelIdentifier> originalId = let(() -> new StringIdentifier("Existing Model"));

        beforeEach(() -> {
          model.get().assignId(originalId.get());
        });

        final Supplier<Object> clone = let(() -> model.get().clone());

        it("creates a distinct instance of the same type", () -> {
          assertThat(clone.get()).isExactlyInstanceOf(TestModel.class);
          assertThat(clone.get()).isNotSameAs(model.get());
        });

        it("performs a shallow copy of the state of the model", () -> {
          assertThat(((TestModel)clone.get()).testField).isEqualTo("abc123");
        });

        it("gives the clone a placeholder / empty model ID", () -> {
          final Model originalModel = model.get(),
                      cloneModel    = (TestModel)clone.get();

          assertThat(originalModel.getId()).isSameAs(originalId.get());
          assertThat(originalModel.isNew()).isFalse();

          assertThat(cloneModel.getId()).isNotEqualTo(originalId.get());
          assertThat(cloneModel.isNew()).isTrue();
        });
      });
    });

    describe("JSON serialization behavior", () -> {
      context("a model that has no relationships", () -> {
        final Supplier<Model> model = let(() -> {
          final TestModel result = new TestModel();

          result.testField = "abc123";

          return result;
        });

        final Supplier<String> modelJson = let(() -> JSONUtils.toJsonString(model.get()));

        it("can be serialized to JSON-API-compliant JSON", () -> {
          assertThat(modelJson.get())
            .isEqualTo("{\"data\":{\"type\":\"test\",\"attributes\":{\"test_field\":\"abc123\"}}}");
        });
      });

      context("a model that has relationships", () -> {
        final Supplier<TestModel> model1 = let(() -> {
          final TestModel result = new TestModel();

          result.testField = "abc123";

          result.assignId(new LongIdentifier(1));

          return result;
        });

        final Supplier<Model> model2 = let(() -> {
          final OtherTestModel result = new OtherTestModel();

          result.otherModel = model1.get();
          result.testField2 = "batman";

          result.assignId(new LongIdentifier(2));

          return result;
        });

        final Supplier<ResourceConverter> resourceConverter =
          let(() -> JSONUtils.createResourceConverterThatIncludesRelationshipsFor(
            TestModel.class,
            OtherTestModel.class));

        final Supplier<String> modelJson = let(() -> JSONUtils.toJsonString(model2.get(), resourceConverter.get()));

        it("can be serialized along with its relationships to JSON-API-compliant JSON", () -> {
          assertThat(modelJson.get())
            .isEqualTo(
              "{\"data\":{\"type\":\"other_test\",\"id\":\"2\",\"attributes\":{\"test_field2\":\"batman\"},"
              + "\"relationships\":{\"other_model\":{\"data\":{\"type\":\"test\",\"id\":\"1\"}}}},"
              + "\"included\":[{\"type\":\"test\",\"id\":\"1\",\"attributes\":{\"test_field\":\"abc123\"}}]}");
        });
      });
    });

    describe("JSON deserialization behavior", () -> {
      final Supplier<ResourceConverter> converter =
        let(() -> JSONUtils.createResourceConverterFor(TestModel.class, OtherTestModel.class));

      context("JSON-API-compliant JSON that represents a model that has no relationships", () -> {
        final String jsonValue =
          "{\"data\":{\"id\":6,\"type\":\"test\",\"attributes\":{\"test_field\":\"abc123\"}}}";

        it("can be de-serialized", () -> {
          final TestModel testModel =
            JSONUtils.fromJsonString(jsonValue, TestModel.class, converter.get());

          assertThat(testModel).isNotNull();
          assertThat(testModel.getId()).isEqualTo(new LongIdentifier(6));
          assertThat(testModel.testField).isEqualTo("abc123");
        });
      });

      context("JSON-API-compliant JSON that represents a model and its relationships", () -> {
        final String jsonValue =
          "{\"data\":{\"type\":\"other_test\",\"id\":\"2\",\"attributes\":{\"test_field2\":\"batman\"},"
          + "\"relationships\":{\"other_model\":{\"data\":{\"type\":\"test\",\"id\":\"1\"}}}},"
          + "\"included\":[{\"type\":\"test\",\"id\":\"1\",\"attributes\":{\"test_field\":\"abc123\"}}]}";

        it("can be de-serialized", () -> {
          final OtherTestModel otherTestModel;
          final TestModel      testModel;

          otherTestModel =
            JSONUtils.fromJsonString(jsonValue, OtherTestModel.class, converter.get());

          assertThat(otherTestModel).isNotNull();
          assertThat(otherTestModel.getId()).isEqualTo(new LongIdentifier(2));
          assertThat(otherTestModel.testField2).isEqualTo("batman");

          testModel = otherTestModel.otherModel;

          assertThat(testModel).isNotNull();
          assertThat(testModel.getId()).isEqualTo(new LongIdentifier(1));
          assertThat(testModel.testField).isEqualTo("abc123");
        });
      });
    });
  }

  @Type("test")
  private static class TestModel
  extends AbstractModel
  implements Cloneable {
    public String testField;

    @Override
    public TestModel clone() throws CloneNotSupportedException {
      return (TestModel)super.clone();
    }
  }

  @Type("other_test")
  private static class OtherTestModel
  extends AbstractModel {
    public String testField2;

    @Relationship("other_model")
    @JsonIgnore
    public TestModel otherModel;
  }
}
