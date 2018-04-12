package com.rosieapp.services.common.model.reflection;

import com.google.common.cache.Cache;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.util.CacheFactory;
import com.rosieapp.util.stream.Collectors;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object for obtaining run-time class information -- via Java Reflection -- about model types
 * that are constructed by Annotation-based Model Builders.
 *
 * <p>This class primarily supports the use case of locating each field in the target model type
 * that has been annotated as a
 * {@link com.rosieapp.services.common.model.annotation.BuilderPopulatedField}.
 *
 * @param <M>
 *        The type of model being built.
 */
public class ModelTypeReflector<M> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelTypeReflector.class);

  /**
   * A cache that increases the performance of looking up what fields to populate for a given model
   * type.
   *
   * <p>The cache is configured to store fields for no more than 32 builder classes at a time, and
   * evicts entries after 10 minutes of use. This helps to ensure high throughput on payloads that
   * are processing the same types of models over and over.
   */
  private static final Cache<String, Map<String, Field>> MODEL_FIELDS_CACHE;

  private final Class<? extends M> modelType;

  static {
    MODEL_FIELDS_CACHE = CacheFactory.createSmallShortTermCache();
  }

  /**
   * Constructor for {@code ModelTypeReflector}.
   *
   * <p>Initializes a new reflector that provides information pertaining to the given specified type
   * of model.
   *
   * @param modelType
   *        The type of builder for which the reflector will provide information.
   */
  public ModelTypeReflector(final Class<? extends M> modelType) {
    this.modelType = modelType;
  }

  /**
   * Gets a map of all builder-populated fields in the model type and its superclasses.
   *
   * <p>For performance, this method stores its results in a short-term cache that is shared across
   * all {@link ModelTypeReflector} instances. The first call to this method employs expensive
   * reflection calls, while subsequent calls for reflectors that wrap the same type of model will
   * return the cached value and not incur the same reflection cost. The cache is set to expire
   * after 10 minutes. In addition, the oldest entry in the cache is expired if the cache grows to
   * more than 32 entries.
   *
   * @return  The map of builder-populated fields. The key of each entry is the field name, and the
   *          value is the field object.
   */
  public Map<String, Field> getAllBuilderPopulatedFields() {
    Map<String, Field> fields = this.getCachedTargetFields();

    if (fields == null) {
      fields = this.identifyBuilderPopulatedFields();

      MODEL_FIELDS_CACHE.put(this.modelType.getCanonicalName(), fields);
    }

    return fields;
  }

  /**
   * Invokes the private constructor for the model via reflection.
   *
   * @return  A new instance of the model type this reflector wraps.
   */
  public M instantiateModel() {
    final M model;

    try {
      final Constructor<? extends M> constructor = this.modelType.getDeclaredConstructor();

      constructor.setAccessible(true);

      model = constructor.newInstance();
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException("Could not instantiate an instance of the model.", ex);
    }

    return model;
  }

  /**
   * Attempts to re-use a cached look-up of target fields for the target model, if it exists.
   *
   * @return  Either the cached map of the target fields in the specified class.
   */
  private Map<String, Field> getCachedTargetFields() {
    final Map<String, Field> fields;
    final Class<? extends M> modelClass = this.modelType;

    fields = MODEL_FIELDS_CACHE.getIfPresent(modelClass.getCanonicalName());

    if (LOGGER.isTraceEnabled()) {
      if (fields == null) {
        LOGGER.trace("No cached fields for model type `{0}`.", modelClass.getCanonicalName());
      } else {
        LOGGER.trace(
          "Resolved fields for model type `{0}` using cache.", modelClass.getCanonicalName());
      }
    }

    return fields;
  }

  /**
   * Determine the builder-populated fields in the target class and all of its parent classes.
   *
   * @return  A map of all of the fields that the builder must populate, keyed by field name.
   */
  private Map<String, Field> identifyBuilderPopulatedFields() {
    final Map<String, Field>  result;
    final List<Field>         fieldList    = new LinkedList<>();
    Class<?>                  currentClass = this.modelType;

    while (currentClass != null) {
      final Class<?> superClass = currentClass.getSuperclass();

      fieldList.addAll(Arrays.asList(currentClass.getDeclaredFields()));

      if (Model.class.isAssignableFrom(superClass)) {
        currentClass = superClass;
      } else {
        currentClass = null;
      }
    }

    result =
      fieldList.stream()
        .filter((field) -> field.isAnnotationPresent(BuilderPopulatedField.class))
        .collect(
          Collectors.toLinkedMap(
            Field::getName,
            Function.identity()));

    return result;
  }
}


