/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.util;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A utility class for getting a shallow copy of an object, if the object can be duplicated.
 *
 * <p>This can be used to perform defensive, "safe" copies in setters and builders -- to ensure
 * that there is no way to manipulate the internal state of an object from outside that object.
 */
public final class ObjectCopier {
  /**
   * A reference to the unique class that is returned by {@code Arrays.asList()}.
   *
   * <p>The class is private, so there is otherwise no other way to get a reference to it.
   *
   * @see Arrays.ArrayList
   * @see Arrays#asList(Object[])
   */
  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  private static final Class<?> ARRAYS_AS_LIST_TYPE = Arrays.asList().getClass();

  /**
   * A reference to the unique class that is returned by {@code Collections.singletonList()}.
   *
   * <p>The class is private, so there is otherwise no other way to get a reference to it.
   *
   * @see Collections.SingletonList
   * @see Collections#singletonList(Object)
   */
  private static final Class<?> SINGLETON_LIST_TYPE = Collections.singletonList(null).getClass();

  /**
   * A reference to the unique class that is returned by {@code Collections.singleton()}.
   *
   * <p>The class is private, so there is otherwise no other way to get a reference to it.
   *
   * @see Collections.SingletonSet
   * @see Collections#singleton(Object)
   */
  private static final Class<?> SINGLETON_SET_TYPE = Collections.singleton(null).getClass();

  /**
   * A reference to the unique class that is returned by {@code Collections.singletonMap()}.
   *
   * <p>The class is private, so there is otherwise no other way to get a reference to it.
   *
   * @see Collections.SingletonMap
   * @see Collections#singletonMap(Object, Object)
   */
  private static final Class<?> SINGLETON_MAP_TYPE =
    Collections.singletonMap(null, null).getClass();

  /**
   * The different strategies employed to copy different types of objects.
   */
  private static final ImmutableMap<Class<?>, Function<Object, Object>> COPY_FUNCTIONS =
    new ImmutableMap.Builder<Class<?>, Function<Object, Object>>()
      .put(ARRAYS_AS_LIST_TYPE, ObjectCopier::copyArraysAsList)
      .put(SINGLETON_LIST_TYPE, ObjectCopier::copySingletonList)
      .put(SINGLETON_SET_TYPE,  ObjectCopier::copySingletonSet)
      .put(Set.class,           ObjectCopier::copySet)
      .put(SINGLETON_MAP_TYPE,  ObjectCopier::copySingletonMap)
      .put(Map.class,           ObjectCopier::copyMap)
      .put(Collection.class,    ObjectCopier::copyCollection)
      .put(Cloneable.class,     ObjectCopier::cloneObject)
      .put(Object.class,        Function.identity())
      .build();

  /**
   * Obtains a copy of the specified object.
   *
   * <p>Different strategies are used for each object type:
   * <ul>
   *   <li>Normal maps and collections are copied to a new instance of the same collection type via
   *       the copy constructor provided by the appropriate object type.</li>
   *   <li>Static collections (e.g. as returned by {@link Arrays#asList(Object[])} or
   *       {@link java.util.Collections#singletonList(Object)} are cloned via
   *       {@link Arrays#asList}.</li>
   *   <li>Objects that implement {@link Cloneable} are cloned.</li>
   *   <li>Objects that do not implement {@link Cloneable} are used in-place, as is.</li>
   * </ul>
   *
   * @param   source
   *          The object to copy. Can be {@code null}.
   *
   * @param   <T>
   *          The type of object.
   *
   * @return  - If the object can be copied, a copy of the object
   *          - If the object is {@code null}, then {@code null}.
   *          - Otherwise, {@code null}.
   */
  public static <T> T copy(final T source) {
    final T result;

    result =
      Optional
        .ofNullable(source)
        .map((nonNullSource) -> getHandlerFor(nonNullSource).apply(nonNullSource))
        .orElse(null);

    return result;
  }

  /**
   * Gets each of the different strategies employed to copy different types of objects.
   *
   * <p>Strategies are ordered from most specific to least specific. The last handler is for
   * {@link Object}, to ensure that there is always a handler for every type of object provided.
   *
   * @return  An ordered map, where each key is an assignable class type, and the value is the
   *          method to call to handle that type of object.
   */
  private static Map<Class<?>, Function<Object, Object>> getCopyFunctions() {
    return COPY_FUNCTIONS;
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
      getCopyFunctions()
        .entrySet()
        .stream()
        .filter(
          (entry) -> entry.getKey().isInstance(source))
        .map(Entry::getValue)
        .findFirst()
        .orElseThrow(
          // Should never happen -- the highest-level fallback in the functions list is for the
          // Object class.
          () -> new IllegalStateException(
            MessageFormat.format(
              "No copy function found for object of type `{0}`",
              source.getClass().getCanonicalName())));

    return (Function<T, T>)typeHandler;
  }

  /**
   * Attempts to copy a list that was previously produced via {@code Arrays.asList()}.
   *
   * @see Arrays.ArrayList
   * @see Arrays#asList(Object[])
   *
   * @param   source
   *          The source list to copy.
   *
   * @return  A copy of the list.
   */
  @SuppressWarnings("unchecked")
  private static Object copyArraysAsList(final Object source) {
    final List<Object> sourceList = (List<Object>)source;

    return Arrays.asList(sourceList.toArray(new Object[0]));
  }

  /**
   * Attempts to copy a list that was previously produced via {@code Collections.singletonList()}.
   *
   * @see Collections.SingletonList
   * @see Collections#singletonList(Object)
   *
   * @param   source
   *          The source list to copy.
   *
   * @return  A copy of the list.
   */
  @SuppressWarnings("unchecked")
  private static Object copySingletonList(final Object source) {
    final List<Object> sourceList = (List<Object>)source;
    final Object       item       = extractSingletonItem(sourceList);

    return Collections.singletonList(item);
  }

  /**
   * Attempts to copy a set that was previously produced via {@code Collections.singleton()}.
   *
   * @see Collections.SingletonSet
   * @see Collections#singleton(Object)
   *
   * @param   source
   *          The source set to copy.
   *
   * @return  A copy of the set.
   */
  @SuppressWarnings("unchecked")
  private static Object copySingletonSet(final Object source) {
    final Set<Object> sourceList  = (Set<Object>)source;
    final Object      item        = extractSingletonItem(sourceList);

    return Collections.singleton(item);
  }

  /**
   * Attempts to copy a set using either a copy constructor or wrapping it in a {@code HashSet}.
   *
   * <p>The "copy constructor" is the constructor on a given {@link Set} type that accepts another
   * set as its only argument.
   *
   * @param   source
   *          The source set to copy.
   *
   * @return  A copy of the set.
   */
  @SuppressWarnings("unchecked")
  private static Object copySet(final Object source) {
    return invokeCopyConstructorOrFallback(
      source, (object) -> new HashSet<>((Set<Object>)source));
  }

  /**
   * Attempts to copy a collection using either a copy constructor or wrapping it in an
   * {@code ArrayList}.
   *
   * <p>The "copy constructor" is the constructor on a given {@link Collection} type that accepts
   * another collection as its only argument.
   *
   * @param   source
   *          The source collection to copy.
   *
   * @return  A copy of the collection.
   */
  @SuppressWarnings("unchecked")
  private static Object copyCollection(final Object source) {
    return invokeCopyConstructorOrFallback(
      source, (object) -> new ArrayList<>((Collection<Object>)source));
  }

  /**
   * Attempts to copy a map that was previously produced via {@code Collections.singletonMap()}.
   *
   * @see Collections.SingletonMap
   * @see Collections#singletonMap(Object, Object)
   *
   * @param   source
   *          The source map to copy.
   *
   * @return  A copy of the map.
   */
  @SuppressWarnings("unchecked")
  private static Object copySingletonMap(final Object source) {
    final Map<Object, Object>   sourceMap = (Map<Object, Object>)source;
    final Entry<Object, Object> item      = extractSingletonItem(sourceMap.entrySet());

    return Collections.singletonMap(item.getKey(), item.getValue());
  }

  /**
   * Attempts to copy a map using either a copy constructor or wrapping it in a {@code HashMap}.
   *
   * <p>The "copy constructor" is the constructor on a given {@link Map} type that accepts another
   * map as its only argument.
   *
   * @param   source
   *          The source map to copy.
   *
   * @return  A copy of the map.
   */
  @SuppressWarnings("unchecked")
  private static Object copyMap(final Object source) {
    return invokeCopyConstructorOrFallback(
      source, (object) -> new HashMap<>((Map<Object, Object>)source));
  }

  /**
   * Attempts to locate and apply a copy constructor for the given object, resorting to the provided
   * fallback to generate a default copy if a copy constructor cannot be invoked.
   *
   * <p>Fallbacks almost always provide a copy that is a generic substitute rather than the same
   * type of class as the source. For example, the generic fallback this class uses for Collection
   * types is {@link ArrayList}, which would used for any collection type that lacks a copy
   * constructor (e.g. the Guava ImmutableList). The resulting copy has all the same elements but
   * is not the same type, nor does it behave the same as the original (immutable vs writable).
   * For this reason, a copy constructor is more desirable because it can guarantee that the copy
   * is of the same type as the source object.
   *
   * @param   source
   *          The object to copy.
   * @param   fallback
   *          A function that receives the object and returns a default copy of it. This is only
   *          invoked if the object's class does not provide a copy constructor.
   *
   * @return  A copy of the source object, obtained either via a copy constructor or the fallback.
   */
  private static Object invokeCopyConstructorOrFallback(final Object source,
                                                        final Function<Object, Object> fallback) {
    Object copy = invokeCopyConstructor(source);

    if (copy == null) {
      // Fallback for the absence of a copy constructor
      copy = fallback.apply(source);
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
    final Constructor<?>  copyConstructor;

    copyConstructor = findCopyConstructor(sourceType);

    if (copyConstructor == null) {
      // No copy constructor available.
      copy = null;
    } else {
      try {
        copy = copyConstructor.newInstance(source);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
        throw new IllegalStateException(
          MessageFormat.format(
            "Failed to invoke copy constructor on `{0}`",
            sourceType.getCanonicalName()),
          ex);
      }
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
      throw new IllegalArgumentException(
        MessageFormat.format(
          "Failed to clone object of type `{0}`",
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
          "Could not find a public clone() method within `{0}`",
          objectType.getCanonicalName()),
        ex);
    }

    return cloneMethod;
  }

  /**
   * Fetches and returns the sole item of a singleton collection.
   *
   * @param   collection
   *          The singleton collection from which a value is desired.
   * @param   <T>
   *          The type of elements in the collection.
   *
   * @return  The singleton value.
   *
   * @throws  IllegalArgumentException
   *          If the singleton collection has no elements (should not happen).
   */
  private static <T> T extractSingletonItem(final Collection<T> collection)
  throws IllegalArgumentException {
    final T result;

    result =
      collection
        .stream()
        .findFirst()
        .orElseThrow(
          // Should never happen -- singletons, by definition, CANNOT be empty.
          () -> new IllegalArgumentException(
            MessageFormat.format(
              "A `{0}` cannot be empty",
              collection.getClass().getSimpleName())));

    return result;
  }

  /**
   * Private constructor for singleton class.
   */
  private ObjectCopier() {
  }
}
