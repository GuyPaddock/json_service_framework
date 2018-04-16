package com.rosieapp.services.common.model.reflection;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.eagerLet;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.replace;

import com.google.common.cache.Cache;
import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
@PrepareForTest({
  ModelTypeReflector.class,
  Cache.class,
  LoggerFactory.class,
  Logger.class
})
public class ModelTypeReflectorTest {
  {
    final Supplier<Logger> testLogger = eagerLet(() -> mock(Logger.class));

    beforeEach(() -> {
      mockStatic(LoggerFactory.class);

      // Stub out logging at TRACE level
      when(LoggerFactory.getLogger(ModelTypeReflector.class)).thenReturn(testLogger.get());
      when(testLogger.get().isTraceEnabled()).thenReturn(true);
    });

    final String testModelClassName =
      "com.rosieapp.services.common.model.reflection.ModelTypeReflectorTest.TestModel";

    final Supplier<Cache<String, Map<String, Field>>> cache =
      eagerLet(() -> {
        final Cache<String, Map<String, Field>> realCache,
                                                spyCache;

        realCache = Whitebox.invokeMethod(ModelTypeReflector.class, "getModelFieldsCache");
        spyCache  = spy(realCache);

        return spyCache;
      });

    beforeEach(() -> {
      replace(method(ModelTypeReflector.class, "getModelFieldsCache"))
        .with((_reflector, _method, _arguments) -> {
          return cache.get();
        });
    });

    afterEach(() -> {
      validateMockitoUsage();
    });

    describe("#ModelTypeReflector(Class)", () -> {
      context("when given `null`", () -> {
        it("throws a `NullPointerException`", () -> {
          assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> {
              new ModelTypeReflector<TestModel>(null);
            })
            .withMessage("modelType cannot be null")
            .withNoCause();
        });
      });

      context("when given a model type does not have a canonical name", () -> {
        class ModelWithoutCanonicalName
        extends AbstractModel {
        }

        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              @SuppressWarnings({"unchecked", "unused" })
              final ModelTypeReflector<ModelWithoutCanonicalName> localReflector =
                new ModelTypeReflector<>(ModelWithoutCanonicalName.class);
            })
            .withMessage(
              "The provided class (`com.rosieapp.services.common.model.reflection"
                + ".ModelTypeReflectorTest$1ModelWithoutCanonicalName`) does not have a canonical "
                + "name. This typically indicates that the class has been declared as an anonymous "
                + "inner class, which is not supported.")
            .withNoCause();
        });
      });
    });

    describe("#getAllBuilderPopulatedFields", () -> {
      final Supplier<ModelTypeReflector<TestModel>> reflector =
        let(() -> spy(new ModelTypeReflector<>(TestModel.class)));

      final Supplier<Map<String, Field>> result =
        let(() -> reflector.get().getAllBuilderPopulatedFields());

      context("when the fields for the model type are not cached", () -> {
        beforeEach(() -> {
          // Force the model type not to be cached
          when(cache.get().getIfPresent(testModelClassName)).thenReturn(null);
        });

        it("returns a field list that matches the model", () -> {
          assertThat(result.get()).containsOnlyKeys("requiredField", "optionalField");
          assertThat(result.get()).doesNotContainValue(null);

          assertThat(result.get().get("requiredField").getName()).isEqualTo("requiredField");
          assertThat(result.get().get("optionalField").getName()).isEqualTo("optionalField");
        });

        it("attempts to find the fields for the model type in the cache", () -> {
          reflector.get().getAllBuilderPopulatedFields();

          verify(cache.get()).getIfPresent(testModelClassName);
        });

        it("writes the model type to the cache", () -> {
          reflector.get().getAllBuilderPopulatedFields();

          verify(cache.get())
            .put(eq(testModelClassName), argThat((arg) -> {
              assertThat(arg).containsOnlyKeys("requiredField", "optionalField");
              assertThat(arg).doesNotContainValue(null);

              assertThat(arg.get("requiredField").getName()).isEqualTo("requiredField");
              assertThat(arg.get("optionalField").getName()).isEqualTo("optionalField");

              return true;
            }));
        });
      });

      context("when the fields for the model type are cached", () -> {
        beforeEach(() -> {
          // Prime the cache
          new ModelTypeReflector<>(TestModel.class).getAllBuilderPopulatedFields();

          // Ignore cache writes caused by priming
          //noinspection unchecked
          reset(cache.get());
        });

        it("returns a field list that matches the model", () -> {
          assertThat(result.get()).containsOnlyKeys("requiredField", "optionalField");
          assertThat(result.get()).doesNotContainValue(null);

          assertThat(result.get().get("requiredField").getName()).isEqualTo("requiredField");
          assertThat(result.get().get("optionalField").getName()).isEqualTo("optionalField");
        });

        it("does not use reflection to identify the fields", () -> {
          reflector.get().getAllBuilderPopulatedFields();

          verifyPrivate(reflector.get(), never()).invoke("identifyBuilderPopulatedFields");
        });

        it("does not write to the cache", () -> {
          reflector.get().getAllBuilderPopulatedFields();

          verify(cache.get(), never()).put(anyString(), any());
        });
      });
    });

    describe("#instantiateModel", () -> {
      context("when the model has a default, private constructor", () -> {
        final Supplier<ModelTypeReflector<TestModel>> reflector =
          let(() -> spy(new ModelTypeReflector<>(TestModel.class)));

        it("instantiates and returns an instance of the model", () -> {
          assertThat(reflector.get().instantiateModel()).isInstanceOf(TestModel.class);
        });
      });

      context("when the model has no default constructor", () -> {
        final Supplier<ModelTypeReflector<ModelWithNoDefaultConstructor>> reflector =
          let(() -> new ModelTypeReflector<>(ModelWithNoDefaultConstructor.class));

        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              reflector.get().instantiateModel();
            })
            .withMessage(
              "Could not instantiate an instance of the model type "
              + "`com.rosieapp.services.common.model.reflection.ModelTypeReflectorTest"
              + ".ModelWithNoDefaultConstructor`")
            .withCauseExactlyInstanceOf(NoSuchMethodException.class);
        });
      });

      context("when the model has a constructor that throws an exception", () -> {
        final Supplier<ModelTypeReflector<ModelWithBadDefaultConstructor>> reflector =
          let(() -> new ModelTypeReflector<>(ModelWithBadDefaultConstructor.class));

        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              reflector.get().instantiateModel();
            })
            .withMessage(
              "Could not instantiate an instance of the model type "
              + "`com.rosieapp.services.common.model.reflection.ModelTypeReflectorTest"
              + ".ModelWithBadDefaultConstructor`")
            .withCauseExactlyInstanceOf(InvocationTargetException.class);
        });
      });
    });
  }

  @SuppressWarnings("unused")
  private static class TestModel
  extends AbstractModel {
    @BuilderPopulatedField(required = true)
    public String requiredField;

    @BuilderPopulatedField
    public String optionalField;

    public String internalField;

    public static class Builder
    extends AnnotationBasedModelBuilder<TestModel, Builder> {
    }
  }

  private static class ModelWithNoDefaultConstructor
  extends AbstractModel {
    @SuppressWarnings("unused")
    private ModelWithNoDefaultConstructor(final String _unused) {
      super();
    }
  }

  private static class ModelWithBadDefaultConstructor
  extends AbstractModel {
    private ModelWithBadDefaultConstructor() {
      super();
      throw new UnsupportedOperationException("Mock, mock!");
    }
  }
}
