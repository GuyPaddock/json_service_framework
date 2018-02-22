/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.fieldhandling;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

/**
 * A utility class for getting a shallow copy of an object, if the object can be duplicated.
 *
 * <p>This is used to provide a defensive, "safe" field value -- a value that is not shared among
 * multiple instances of the same model.
 */
public final class ObjectCopier {
  /**
   * The different strategies employed to copy different types of objects.
   */
  static final ImmutableMap<Class<?>, Function<Object, Object>> COPY_FUNCTIONS =
    new ImmutableMap.Builder<Class<?>, Function<Object, Object>>()
      .put(Map.class,         ObjectCopier::copyMap)
      .put(Collection.class,  ObjectCopier::copyCollection)
      .put(Cloneable.class,   ObjectCopier::cloneObject)
      .put(Object.class,      Function.identity())
      .build();

  /**
   * Obtains a copy of the specified object.
   *
   * <p>Different strategies are used for each object type:
   * <ul>
   *   <li>Maps and collections are copied to a new instance of the same collection type via the
   *       copy constructor provided by the appropriate object type.</li>
   *   <li>Objects that implement {@link Cloneable} are cloned.</li>
   *   <li>Objects that do not implement {@link Cloneable} are used in-place, as is.</li>
   * </ul>
   *
   * @param   source
   *          The object to copy.
   *
   * @param   <T>
   *          The type of object.
   *
   * @return  If the object can be copied, a copy of the object; otherwise, the original object.
   */
  public static <T> T copy(final T source) {
    return getHandlerFor(source).apply(source);
  }

  /**
   * Finds the appropriate way to copy the specified object.
   *
   * @param   source
   *          The object that needs to be copied.
   *
   * @param   <T>
   *          The type of object being copied.
   *
   * @return  The best function to invoke to obtain a copy of the object.
   */
  @SuppressWarnings("unchecked")
  private static <T> Function<T, T> getHandlerFor(final T source) {
    Function<Object, Object> typeHandler;

    typeHandler =
      COPY_FUNCTIONS.entrySet().stream()
        .filter((entry) -> entry.getKey().isInstance(source))
        .map(Entry::getValue)
        .findFirst()
        .orElseThrow(
          () -> new IllegalStateException(
            MessageFormat.format(
              "No copy function found for object of type `{0}`.",
              source.getClass().getCanonicalName())));

    return (Function<T, T>)typeHandler;
  }

  /**
   * Attempts to copy a map using either a copy constructor or wrapping it in a {@code HashMap}.
   *
   * @param   source
   *          The source map to copy.
   *
   * @return  A copy of the map.
   */
  @SuppressWarnings("unchecked")
  private static Object copyMap(final Object source) {
    Object copy = invokeCopyConstructor(source);

    if (copy == null) {
      copy = new HashMap<>((Map<Object, Object>)source);
    }

    return copy;
  }

  /**
   * Attempts to copy a collection using either a copy constructor or wrapping it in an
   * {@code ArrayList}.
   *
   * @param   source
   *          The source collection to copy.
   *
   * @return  A copy of the collection.
   */
  @SuppressWarnings("unchecked")
  private static Object copyCollection(final Object source) {
    Object copy = invokeCopyConstructor(source);

    if (copy == null) {
      copy = new ArrayList<>((Collection<Object>)source);
    }

    return copy;
  }

  /**
   * Attempts to generates a shallow copy of an object by invoking its "copy constructor" -- a
   * single-argument constructor that takes in an object of the same type or its supertype.
   *
   * <p>For example, {@link java.util.LinkedList#LinkedList(Collection)} is a copy constructor for
   * lists.
   *
   * @param   source
   *          The object to copy.
   *
   * @return  A copy of the object; or, {@code null} if the object does not have a copy constructor.
   *
   * @throws  IllegalStateException
   *          If the copy constructor somehow fails to be invokable. This typically indicates a
   *          code defect in the underlying object type.
   */
  private static Object invokeCopyConstructor(final Object source)
  throws IllegalStateException {
    final Object          copy;
    final Class<?>        sourceType       = source.getClass();
    final Constructor<?>  mapConstructor;

    mapConstructor = findCopyConstructor(sourceType);

    if (mapConstructor != null) {
      try {
        copy = mapConstructor.newInstance(source);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
        throw new IllegalStateException(
          MessageFormat.format(
            "Failed to invoke copy constructor on {0}.",
            sourceType.getCanonicalName()),
          ex);
      }
    } else {
      // No copy constructor available.
      copy = null;
    }

    return copy;
  }

  /**
   * Generates a shallow copy of an object by invoking its {@code clone()} method.
   *
   * @param   source
   *          The object to copy.
   *
   * @return  A copy of the object.
   *
   * @throws  IllegalStateException
   *          If the clone method somehow fails to be invokable. This typically indicates a code
   *          defect in the underlying object type.
   */
  private static Object cloneObject(final Object source)
  throws IllegalStateException {
    final Class<?>  sourceType = source.getClass();
    final Method    cloneMethod = findCloneMethod(sourceType);
    final Object    copy;

    try {
      copy = cloneMethod.invoke(source);
    } catch (IllegalAccessException | InvocationTargetException ex) {
      throw new IllegalStateException(
        MessageFormat.format(
          "Failed to clone object of type `{0}`.",
          sourceType.getCanonicalName()),
        ex);
    }

    return copy;
  }

  /**
   * Finds the public copy constructor within the specified class.
   *
   * <p>If the object does not have a public, single-argument constructor that accepts the same type
   * of object or its super-type, then {@code null} is returned instead.
   *
   * @param   objectType
   *          The type of object being cloned.
   *
   * @return  Either the public copy constructor for the object type; or, {@code null} if the object
   *          does not have a copy constructor.
   */
  private static Constructor<?> findCopyConstructor(final Class<?> objectType)
  throws IllegalArgumentException {
    final Constructor<?> mapConstructor;

    mapConstructor = Arrays.stream(objectType.getConstructors())
      .filter(ObjectCopier::isCopyConstructor)
      .findFirst()
      .orElse(null);

    return mapConstructor;
  }

  /**
   * Determines whether or not the provided constructor is a copy constructor for its declaring
   * type.
   *
   * <p>A constructor is considered a copy constructor if and only if all of the following are true:
   * <ul>
   *   <li>The constructor is public.</li>
   *   <li>The constructor takes a single argument.</li>
   *   <li>The type of argument the constructor takes is either the same as the class defining the
   *       constructor, or one of its superclasses.</li>
   * </ul>
   *
   * @param   constructor
   *          The constructor to evaluate.
   *
   * @return  {@code true} if the constructor is a copy constructor; or, {@code false} if it is
   *          not.
   */
  private static boolean isCopyConstructor(final Constructor<?> constructor) {
    final boolean   result;
    final Class<?>  objectType = constructor.getDeclaringClass();

    result =
      (constructor.getParameterCount() == 1)
      && Arrays.stream(constructor.getParameterTypes())
           .allMatch((param) -> param.isAssignableFrom(objectType));

    return result;
  }

  /**
   * Finds the public {@code clone()} method within the specified class.
   *
   * <p>The object must have a public {@link #clone()} method, or an
   * {@link IllegalArgumentException} will be thrown.
   *
   * @param   objectType
   *          The type of object being cloned.
   *
   * @return  The public {@code clone()} method on the object.
   *
   * @throws  IllegalArgumentException
   *          If the provided object type does not have a public {@code clone()} method.
   */
  private static Method findCloneMethod(final Class<?> objectType)
  throws IllegalArgumentException {
    final Method cloneMethod;

    try {
      cloneMethod = objectType.getMethod("clone");
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException(
        MessageFormat.format(
          "Could not find a public clone() method within {0}.",
          objectType.getCanonicalName()),
        ex);
    }

    return cloneMethod;
  }

  /**
   * Private constructor for singleton class.
   */
  private ObjectCopier() {
  }

  // FIXME: Convert this into multiple different unit tests (RJAJ-6)
  public static void main(final String[] args) {
    List<String>        testList1 = Arrays.asList("a", "b", "c"),
                        listCopy1 = ObjectCopier.copy(testList1),
                        testList2 = new LinkedList<>(Arrays.asList("a", "b", "c")),
                        listCopy2 = ObjectCopier.copy(testList2),
                        testList3 = Collections.singletonList("a"),
                        listCopy3 = ObjectCopier.copy(testList3);
    Map<String, String> testMap  = new HashMap<>(),
                        mapCopy;
    Attribute           cloneableObject       = new BasicAttribute("Test", true),
                        cloneableCopy         = ObjectCopier.copy(cloneableObject);
    Object              uncloneableObject     = "Some string",
                        uncloneableObjectCopy = ObjectCopier.copy(uncloneableObject);

    testMap.put("1", "A");
    testMap.put("2", "B");
    testMap.put("3", "C");

    mapCopy = ObjectCopier.copy(testMap);

    System.out.println(
      "testList1 == listCopy1: " + (testList1 == listCopy1));
    System.out.println(
      "testList1.get(0) == listCopy1.get(0): " + (testList1.get(0) == listCopy1.get(0)));

    System.out.println(
      "testList2 == listCopy2: " + (testList2 == listCopy2));
    System.out.println(
      "testList2.get(0) == listCopy2.get(0): " + (testList2.get(0) == listCopy2.get(0)));

    System.out.println(
      "testList3 == listCopy3: " + (testList3 == listCopy3));
    System.out.println(
      "testList3.get(0) == listCopy3.get(0): " + (testList3.get(0) == listCopy3.get(0)));

    System.out.println(
      "testMap == mapCopy: " + (testMap == mapCopy));
    System.out.println(
      "testMap.get(\"1\") == mapCopy.get(\"1\"): " + (testMap.get("1") == mapCopy.get("1")));

    System.out.println(
      "cloneableObject == cloneableCopy: " + (cloneableObject == cloneableCopy));

    System.out.println(
      "uncloneableObject == uncloneableObjectCopy: "
      + (uncloneableObject == uncloneableObjectCopy));
  }
}
