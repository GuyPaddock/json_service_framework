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
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
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
  BuilderTypeReflector.class,
  Cache.class,
  LoggerFactory.class,
  Logger.class
})
public class BuilderTypeReflectorTest {
  {
    final Supplier<Logger> testLogger = eagerLet(() -> mock(Logger.class));

    beforeEach(() -> {
      mockStatic(LoggerFactory.class);

      // Stub out logging at TRACE level
      when(LoggerFactory.getLogger(BuilderTypeReflector.class)).thenReturn(testLogger.get());
      when(testLogger.get().isTraceEnabled()).thenReturn(true);
    });

    afterEach(() -> {
      validateMockitoUsage();
    });

    describe("#BuilderTypeReflector(Class)", () -> {
      context("when given `null`", () -> {
        it("throws a `NullPointerException`", () -> {
          assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> {
              new BuilderTypeReflector<TestModel, TestModel.BuilderWithModelAsGenericParam>(null);
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
              "The provided class (`com.rosieapp.services.common.model.reflection"
              + ".BuilderTypeReflectorTest$1BuilderWithoutCanonicalName`) does not have a "
              + "canonical name. This typically indicates that the class has been declared as an "
              + "anonymous inner class, which is not supported.")
            .withNoCause();
        });
      });
    });

    describe("#getModelClass", () -> {
      final Supplier<Cache<String, Class<? extends Model>>> cache =
        eagerLet(() -> {
          final Cache<String, Class<? extends Model>> realCache,
                                                      spyCache;

          realCache = Whitebox.invokeMethod(BuilderTypeReflector.class, "getModelTypeCache");
          spyCache  = spy(realCache);

          return spyCache;
        });

      beforeEach(() -> {
        replace(method(BuilderTypeReflector.class, "getModelTypeCache"))
          .with((_reflector, _method, _arguments) -> {
            return cache.get();
          });
      });

      describe("normal cases", () -> {
        context("when the builder specifies the model type via generics", () -> {
          final String testModelBuilderName =
            "com.rosieapp.services.common.model.reflection.BuilderTypeReflectorTest.TestModel"
            + ".BuilderWithModelAsGenericParam";

          final Supplier<BuilderTypeReflector> reflector =
            let(() -> {
              return spy(new BuilderTypeReflector<>(
                TestModel.BuilderWithModelAsGenericParam.class));
            });

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
              new BuilderTypeReflector<>(TestModel.BuilderWithModelAsGenericParam.class)
                .getModelClass();

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

        context("when the builder specifies the model type via enclosing class", () -> {
          final String testModelBuilderName =
            "com.rosieapp.services.common.model.reflection.BuilderTypeReflectorTest.TestModel"
            + ".BuilderWithModelAsEnclosingType";

          @SuppressWarnings("unchecked")
          final Supplier<BuilderTypeReflector> reflector =
            let(() -> {
              return spy(new BuilderTypeReflector<>(
                TestModel.BuilderWithModelAsEnclosingType.class));
            });

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
              new BuilderTypeReflector<>(TestModel.BuilderWithModelAsGenericParam.class)
                .getModelClass();

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
    public static class BuilderWithModelAsGenericParam
    extends AnnotationBasedModelBuilder<TestModel, BuilderWithModelAsGenericParam> {
    }

    public static class BuilderWithModelAsEnclosingType
    extends AnnotationBasedModelBuilder {
    }
  }

  private static class NonConformantBuilder
  extends AnnotationBasedModelBuilder {
  }
}
