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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.replace;

import com.google.common.cache.Cache;
import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
import com.rosieapp.util.CacheFactory;
import java.util.function.Supplier;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Spectrum.class)
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates"
})
@PrepareForTest({
  BuilderTypeReflector.class,
  Cache.class
})
public class BuilderTypeReflectorTest {
  {
    afterEach(() -> {
      validateMockitoUsage();
    });

    describe("#BuilderTypeReflector(Class)", () -> {
      context("when given `null`", () -> {
        it("throws a `NullPointerException`", () -> {
          assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> {
              new BuilderTypeReflector<TestModel, TestModel.Builder>(null);
            })
            .withMessage("builderType cannot be null")
            .withNoCause();
        });
      });

      context("when given a builder type does not have a canonical name", () -> {
        class BuilderWithoutCanonicalName
          extends AnnotationBasedModelBuilder {
        }

        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              @SuppressWarnings({"unchecked", "unused"})
              final BuilderTypeReflector reflector =
                new BuilderTypeReflector<>(BuilderWithoutCanonicalName.class);
            })
            .withMessage(
              "The provided builder class (`com.rosieapp.services.common.model.reflection"
                + ".BuilderTypeReflectorTest$1BuilderWithoutCanonicalName`) does not have a "
                + "canonical name. This typically indicates that the provided builder type has "
                + "been declared as an anonymous inner class, which is not supported.")
            .withNoCause();
        });
      });
    });

    describe("#getModelClass", () -> {
      final Supplier<Cache<String, Class<? extends Model>>> cache = eagerLet(() -> {
        final Cache<String, Class<? extends Model>> realCache,
                                                    spyCache;

        realCache = CacheFactory.createSmallShortTermCache();
        spyCache  = spy(realCache);

        return spyCache;
      });

      beforeEach(() -> {
        replace(method(BuilderTypeReflector.class, "getModelTypeCache"))
          .with((builder, method, arguments) -> {
            return cache.get();
          });
      });

      describe("normal cases", () -> {
        final String testModelBuilderName =
          "com.rosieapp.services.common.model.reflection.BuilderTypeReflectorTest.TestModel"
          + ".Builder";

        final Supplier<BuilderTypeReflector> reflector =
          let(() -> spy(new BuilderTypeReflector<>(TestModel.Builder.class)));

        context("when the model type is not cached", () -> {
          beforeEach(() -> {
            // Force the model type not to be cached
            when(cache.get().getIfPresent(testModelBuilderName)).thenReturn(null);
          });

          it("returns the model type that matches the builder", () -> {
            assertThat(reflector.get().getModelClass()).isEqualTo(TestModel.class);
          });

          it("attempts to find the model type in the cache", () -> {
            reflector.get().getModelClass();

            verify(cache.get()).getIfPresent(testModelBuilderName);
          });

          it("writes the model type to the cache", () -> {
            reflector.get().getModelClass();

            verify(cache.get()).put(testModelBuilderName, TestModel.class);
          });
        });

        context("when the model type is cached", () -> {
          beforeEach(() -> {
            // Prime the cache
            new BuilderTypeReflector<>(TestModel.Builder.class).getModelClass();

            // Ignore cache writes caused by priming
            //noinspection unchecked
            reset(cache.get());
          });

          it("returns the model type that matches the builder", () -> {
            assertThat(reflector.get().getModelClass()).isEqualTo(TestModel.class);
          });

          it("does not use reflection to identify the model type", () -> {
            reflector.get().getModelClass();

            verifyPrivate(reflector.get(), never()).invoke("identifyModelClass");
          });

          it("does not write to the cache", () -> {
            reflector.get().getModelClass();

            verify(cache.get(), never()).put(anyString(), any());
          });
        });
      });

      describe("exception cases", () -> {
        context("when it is not possible to infer the type of model built by the builder", () -> {
          it("throws an `IllegalArgumentException`", () -> {
            assertThatExceptionOfType(IllegalArgumentException.class)
              .isThrownBy(() -> {
                @SuppressWarnings("unchecked")
                final BuilderTypeReflector reflector =
                  new BuilderTypeReflector<>(NonConformantBuilder.class);

                reflector.getModelClass();
              })
              .withMessage(
                "The builder is expected either to have a generic parameter that is a sub-class of "
                + "`com.rosieapp.services.common.model.Model`, or it is expected to be declared as "
                + "an inner class of the model it builds.")
              .withNoCause();
          });
        });
      });
    });
  }

  private static class TestModel
  extends AbstractModel {
    public static class Builder
    extends AnnotationBasedModelBuilder<TestModel, Builder> {
    }
  }

  private static class NonConformantBuilder
  extends AnnotationBasedModelBuilder {
  }
}
