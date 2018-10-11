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

package com.rosieapp.util;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.replace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.naming.directory.BasicAttribute;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Spectrum.class)
@PrepareForTest({
  ObjectCopier.class,
  Collections.class
})
@SuppressWarnings({
  "Convert2MethodRef",
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic",
  "Duplicates",
  "PMD.LooseCoupling"
})
public class ObjectCopierTest {
  {
    describe(".copy", () -> {
      final Variable<Object> input = new Variable<>();

      final Supplier<Object> result = let(() -> ObjectCopier.copy(input.get()));

      context("when given `null`", () -> {
        beforeEach(() -> {
          input.set(null);
        });

        it("returns `null`", () -> {
          assertThat(result.get()).isNull();
        });
      });

      context("when given an `Arrays.ArrayList`", () -> {
        beforeEach(() -> {
          input.set(Arrays.asList("b", "a", "c"));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((List<String>)result.get())).containsExactly("b", "a", "c");

          // Verify that it still behaves like a static ArrayList, which does not support add().
          assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> {
              //noinspection unchecked
              ((List<Object>)result.get()).add("test");
            })
            .withNoCause();
        });
      });

      context("when given a `LinkedList`", () -> {
        beforeEach(() -> {
          input.set(new LinkedList<>(Arrays.asList("b", "a", "c")));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((List<String>)result.get())).containsExactly("b", "a", "c");

          // Verify that it still behaves like a LinkedList, which supports add().
          assertThatCode(() -> {
            //noinspection unchecked
            ((List<Object>)result.get()).add("test");
          }).doesNotThrowAnyException();
        });
      });

      context("when given a `HashSet`", () -> {
        beforeEach(() -> {
          input.set(new HashSet<>(Arrays.asList("a", "b", "c", "a")));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((Set<String>)result.get())).containsExactlyInAnyOrder("a", "b", "c");

          // Verify that it still behaves like a LinkedList, which supports add().
          assertThatCode(() -> {
            //noinspection unchecked
            ((Set<Object>)result.get()).add("test");
          }).doesNotThrowAnyException();
        });
      });

      context("when given a `LinkedHashSet`", () -> {
        beforeEach(() -> {
          input.set(new LinkedHashSet<>(Arrays.asList("b", "a", "c", "a")));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((Set<String>)result.get())).containsExactly("b", "a", "c");

          // Verify that it still behaves like a LinkedList, which supports add().
          assertThatCode(() -> {
            //noinspection unchecked
            ((Set<Object>)result.get()).add("test");
          }).doesNotThrowAnyException();
        });
      });

      context("when given a `Collections.SingletonList`", () -> {
        beforeEach(() -> {
          input.set(Collections.singletonList("a"));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((List<String>)result.get())).containsExactly("a");

          // Verify that it still behaves like a singleton list, which does not support add().
          assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> {
              //noinspection unchecked
              ((List<String>)result.get()).add("test");
            })
            .withNoCause();
        });
      });

      context("when given an `ImmutableList`", () -> {
        beforeEach(() -> {
          input.set(ImmutableList.of("b", "a", "c"));
        });

        // I could go either way on whether this is the ideal behavior. For now, it covers our needs
        // and can always be changed in the future.
        it("returns a new ArrayList containing shallow copies of the original set elements", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(ArrayList.class);

          //noinspection unchecked
          assertThat(((List<String>)result.get())).containsExactly("b", "a", "c");
        });
      });

      context("when given a `Collections.SingletonSet`", () -> {
        beforeEach(() -> {
          input.set(Collections.singleton("a"));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((Set<String>)result.get())).containsExactly("a");

          // Verify that it still behaves like a singleton list, which does not support add().
          assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> {
              //noinspection unchecked
              ((Set<String>)result.get()).add("test");
            })
            .withNoCause();
        });
      });

      context("when given an `ImmutableSet`", () -> {
        beforeEach(() -> {
          input.set(ImmutableSet.of("b", "a", "c", "a"));
        });

        // I could go either way on whether this is the ideal behavior. For now, it covers our needs
        // and can always be changed in the future.
        it("returns a new HashSet containing shallow copies of the original set elements", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(HashSet.class);

          //noinspection unchecked
          assertThat(((Set<String>)result.get())).containsExactlyInAnyOrder("b", "a", "c");
        });
      });

      context("when given a `LinkedHashMap`", () -> {
        beforeEach(() -> {
          input.set(
            new LinkedHashMap<>(
              ImmutableMap.of(
                "2", "B",
                "1", "A",
                "3", "C")));
        });

        final Supplier<String> lastKey = let(() -> {
          //noinspection unchecked
          return ((Map<String, String>)result.get())
                   .keySet()
                   .stream()
                   .reduce((first, second) -> second)
                   .orElse(null);
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((Map<String, String>)result.get())).containsExactly(
            entry("2", "B"),
            entry("1", "A"),
            entry("3", "C"));

          // Verify that it still behaves like a Linked Hash Map, which supports put() and ordering.
          assertThatCode(() -> {
            //noinspection unchecked
            ((Map<String, String>)result.get()).put("4", "D");
          }).doesNotThrowAnyException();

          assertThat(lastKey.get()).isEqualTo("4");
        });
      });

      context("when given a `SingletonMap`", () -> {
        beforeEach(() -> {
          input.set(Collections.singletonMap("1", "A"));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());

          //noinspection unchecked
          assertThat(((Map<String, String>)result.get())).containsExactly(entry("1", "A"));

          // Verify that it still behaves like a Singleton Map, which does not support put().
          assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> {
              //noinspection unchecked
              ((Map<String, String>)result.get()).put("test", "value");
            })
            .withNoCause();
        });
      });

      context("when given an `ImmutableMap`", () -> {
        beforeEach(() -> {
          input.set(
            // Thanks a lot, Takei. Now everybody knows!
            ImmutableMap.of(
              "1", "A",
              "2", "B",
              "3", "C"));
        });

        // I could go either way on whether this is the ideal behavior. For now, it covers our needs
        // and can always be changed in the future.
        it("returns a new HashMap containing shallow copies of the original map elements", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(HashMap.class);

          //noinspection unchecked
          assertThat(((Map<String, String>)result.get())).containsOnly(
            entry("1", "A"),
            entry("2", "B"),
            entry("3", "C"));
        });
      });

      context("when given a cloneable object", () -> {
        beforeEach(() -> {
          input.set(new BasicAttribute("Test", true));
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isNotSameAs(input.get());
          assertThat(result.get()).isEqualTo(input.get());
          assertThat(result.get().getClass()).isEqualTo(input.get().getClass());
        });
      });

      context("when given an un-cloneable object", () -> {
        beforeEach(() -> {
          input.set("Some string");
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isSameAs(input.get());
        });
      });

      context("when there is no copy function for the object type (should never happen)", () -> {
        beforeEach(() -> {
          // Force the copy functions to be mis-configured
          replace(method(ObjectCopier.class, "getCopyFunctions"))
            .with((builder, method, arguments) -> {
              return ImmutableMap.of(Boolean.class, Function.identity());
            });
        });

        it("throws an `IllegalStateException`", () -> {
          assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> {
              ObjectCopier.copy("some object");
            })
            .withMessage("No copy function found for object of type `java.lang.String`")
            .withNoCause();
        });
      });

      context("when given a collection with a badly-implemented copy constructor", () -> {
        it("throws an `IllegalStateException`", () -> {
          assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> {
              ObjectCopier.copy(new CollectionWithBadCopyConstructor());
            })
            .withMessage(
              "Failed to invoke copy constructor on "
              + "`com.rosieapp.util.ObjectCopierTest.CollectionWithBadCopyConstructor`")
            .withCauseInstanceOf(InvocationTargetException.class);
        });
      });

      context("when given an object that implements Cloneable without a clone() method", () -> {
        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              ObjectCopier.copy(new CloneableObjectWithoutCloneMethod());
            })
            .withMessage(
              ("Could not find a public clone() method within "
               + "`com.rosieapp.util.ObjectCopierTest.CloneableObjectWithoutCloneMethod`"))
            .withCauseInstanceOf(NoSuchMethodException.class);
        });
      });

      context("when given an object with a badly-implemented clone() method", () -> {
        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              ObjectCopier.copy(new ObjectWithBadCloneMethod());
            })
            .withMessage(
              ("Failed to clone object of type "
               + "`com.rosieapp.util.ObjectCopierTest.ObjectWithBadCloneMethod`"))
            .withCauseInstanceOf(InvocationTargetException.class);
        });
      });

      context("when given an empty singleton list (should never happen)", () -> {
        @SuppressWarnings("unchecked")
        final Supplier<List<String>> badSingletonList = let(() -> mock(List.class));

        beforeEach(() -> {
          final Map<Class<?>, Function<Object, Object>> originalCopyFunctions;
          final Class<?>                                mockType;
          final Function<Object, Object>                copyFunction;

          originalCopyFunctions = Whitebox.invokeMethod(ObjectCopier.class, "getCopyFunctions");

          mockType     = badSingletonList.get().getClass();
          copyFunction = originalCopyFunctions.get(Collections.singletonList(null).getClass());

          // Map the mock type to the copy function for singleton lists.
          replace(method(ObjectCopier.class, "getCopyFunctions"))
            .with((builder, method, arguments) -> {
              return ImmutableMap.of(mockType, copyFunction);
            });
        });

        it("throws an `IllegalArgumentException`", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> {
              ObjectCopier.copy(badSingletonList.get());
            })
            .withMessageMatching("A `List\\$MockitoMock\\$[0-9]*` cannot be empty")
            .withNoCause();
        });
      });

      context("when given an un-cloneable object", () -> {
        beforeEach(() -> {
          input.set("Some string");
        });

        it("returns a new shallow copy of the same type", () -> {
          assertThat(result.get()).isSameAs(input.get());
        });
      });
    });
  }

  private static class CollectionWithBadCopyConstructor
  extends LinkedList<String> {
    public CollectionWithBadCopyConstructor() {
      super();
    }

    @SuppressWarnings("unused")
    public CollectionWithBadCopyConstructor(final CollectionWithBadCopyConstructor other) {
      super();
      throw new UnsupportedOperationException();
    }
  }

  private static class CloneableObjectWithoutCloneMethod
  implements Cloneable {
  }

  private static class ObjectWithBadCloneMethod
  implements Cloneable {
    @Override
    public ObjectWithBadCloneMethod clone()
    throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
    }
  }
}
