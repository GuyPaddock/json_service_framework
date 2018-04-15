package com.rosieapp.services.common.model.reflection;

import com.google.common.cache.Cache;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.AnnotationBasedModelBuilder;
import com.rosieapp.util.CacheFactory;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object for obtaining run-time class information --  via Java Reflection -- about
 * Annotation-based Model Builders.
 *
 * <p>This class primarily supports the use case of inferring the class of model a given
 * annotation-based model builder constructs based on the generic type signature and enclosing class
 * of the model builder. See {@link #getModelClass()} for more information.
 *
 * @param <M>
 *        The type of model that the builder builds.
 * @param <B>
 *        The type of builder class.
 */
public class BuilderTypeReflector<M extends Model, B extends AnnotationBasedModelBuilder<M, B>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BuilderTypeReflector.class);

  /**
   * A cache that increases the performance of looking up what type of model to create for each
   * type of builder.
   *
   * <p>The cache is configured to store no more than 32 model classes at a time, and evicts entries
   * after 10 minutes of use. This helps to ensure high throughput on payloads that are processing
   * the same model fields over and over.
   */
  private static final Cache<String, Class<? extends Model>> MODEL_TYPE_CACHE;

  private Class<B> builderType;

  static {
    MODEL_TYPE_CACHE = CacheFactory.createSmallShortTermCache();
  }

  /**
   * Constructor for {@code BuilderTypeReflector}.
   *
   * <p>Initializes a new reflector that provides information pertaining to the given specified type
   * of builder.
   *
   * <p>The builder must have a canonical name (i.e. it cannot be an anonymous inner class) and the
   * declaration of the builder should follow the conventions described by {@link #getModelClass()}.
   *
   * @param   builderType
   *          The type of builder for which the reflector will provide information. Cannot be
   *          {@code null}.
   *
   * @throws  NullPointerException
   *          If {@code builderType} is {@code null}.
   * @throws  IllegalArgumentException
   *          If the builder class lacks a canonical name. This is typically because the builder
   *          class has been declared as an anonymous inner class.
   */
  public BuilderTypeReflector(final Class<B> builderType)
  throws IllegalArgumentException {
    this.setBuilderType(builderType);
  }

  /**
   * Gets the type of builder being reflected upon.
   *
   * @return  The builder class type.
   */
  private Class<B> getBuilderType() {
    return this.builderType;
  }

  /**
   * Validates and sets the builder type.
   *
   * @param   builderType
   *          The builder type.
   *
   * @throws  NullPointerException
   *          If {@code builderType} is {@code null}.
   * @throws  IllegalArgumentException
   *          If the builder class lacks a canonical name. This is typically because the builder
   *          class has been declared as an anonymous inner class.
   */
  private void setBuilderType(final Class<B> builderType) {
    final String canonicalBuilderName;

    Objects.requireNonNull(builderType, "builderType cannot be null");

    canonicalBuilderName = builderType.getCanonicalName();

    if (canonicalBuilderName == null) {
      throw new IllegalArgumentException(
        MessageFormat.format(
          "The provided builder class (`{0}`) does not have a canonical name. This typically "
          + "indicates that the provided builder type has been declared as an anonymous inner "
          + "class, which is not supported.", builderType.getName()));
    }

    this.builderType = builderType;
  }

  /**
   * Determines the type of model that are built by the builder this reflector wraps.
   *
   * <p>The model type is determined by using either of the following two conventions / patterns in
   * the way the builder has been defined:
   * <ul>
   *   <li><strong>Based on the generic type of the builder:</strong>
   *       Builders typically define the model type as the second generic type parameter when
   *       extending {@link AnnotationBasedModelBuilder}. This information is available to
   *       reflectors at run-time because this is a special case of Java generic semantics in which
   *       type information is not erased at compile-time.</li>
   *   <li><strong>Based on the enclosing class of the builder:</strong>
   *       Builders are almost always defined as static inner classes within the model they build,
   *       since the builder objects take the place of one or more constructors within the model.
   *       If the class that directly encloses a builder is a model, it is assumed that the builder
   *       constructs that model type.</li>
   * </ul>
   *
   * <p>Whenever possible, builder sub-classes should always follow both of the conventions
   * indicated above. If they cannot, they should override either the
   * {@link AnnotationBasedModelBuilder#getModelClass()} or
   * {@link AnnotationBasedModelBuilder#instantiateModel()} methods.
   *
   * <p>For performance, this method stores its results in a short-term cache that is shared across
   * all {@link BuilderTypeReflector} instances. The first call to this method employs expensive
   * reflection calls, while subsequent calls for reflectors that wrap the same type of builder will
   * return the cached value and not incur the same reflection cost. The cache is set to expire
   * after 10 minutes. In addition, the oldest entry in the cache is expired if the cache grows to
   * more than 32 entries.
   *
   * @return  The type of model that the builder creates.
   *
   * @throws  IllegalArgumentException
   *          If the model type cannot be determined. This is typically because the builder class
   *          declaration does not follow either of the conventions indicated above. (It can also
   *          happen when a builder is being mocked out via PowerMock with CGLIB.)
   */
  @SuppressWarnings("unchecked")
  public Class<M> getModelClass()
  throws IllegalArgumentException {
    Class<M> modelClass;

    modelClass = this.getCachedModelType();

    if (modelClass == null) {
      modelClass = this.identifyModelClass();

      this.storeModelInCache(modelClass);
    }

    return modelClass;
  }

  /**
   * Gets the cache of builder class names to model types.
   *
   * @return  The cache, keyed by builder class name. Each value is the type of model that builder
   *          builds.
   */
  private static Cache<String, Class<? extends Model>> getModelTypeCache() {
    return MODEL_TYPE_CACHE;
  }

  /**
   * Gets a unique string that will uniquely identify the class of the builder this reflector is
   * wrapping.
   *
   * <p>The canonical name of the builder class is used for this purpose. This is just a shortcut to
   * having to call {@code this.getBuilderType().getCanonicalName()} throughout this class.
   *
   * @return  The canonical name for the builder type.
   *
   * @throws  IllegalArgumentException
   *          If the builder class lacks a canonical name. This is typically because the builder
   *          class has been declared as an anonymous inner class.
   */
  private String getUniqueBuilderClassName()
  throws IllegalArgumentException {
    return this.getBuilderType().getCanonicalName();
  }

  /**
   * Attempts to return the cached the model type for the builder type this reflector wraps.
   *
   * <p>The model type will already be cached if we have previously looked it up through this
   * reflector instance or another reflector that is wrapping the same builder type.
   *
   * @return  Either the cached model type, if it has previously been resolved; or, {@code null} if
   *          it is not cached.
   */
  @SuppressWarnings("unchecked")
  private Class<M> getCachedModelType() {
    final String    builderClassName  = this.getUniqueBuilderClassName();
    final Class<M>  modelType;

    modelType = (Class<M>)getModelTypeCache().getIfPresent(builderClassName);

    if (LOGGER.isTraceEnabled()) {
      if (modelType == null) {
        LOGGER.trace(
          "No cached model type for builder type `{1}`.", builderClassName);
      } else {
        LOGGER.trace(
          "Resolved model type `{0}` for builder type `{1}` using cache.",
          modelType.getCanonicalName(),
          builderClassName);
      }
    }

    return modelType;
  }

  /**
   * Updates the builder-to-model class cache with the given model type for the builder this
   * reflector is wrapping.
   *
   * @param modelClass
   *        The class that is being cached as the type of model this builder
   *        produces.
   */
  private void storeModelInCache(final Class<M> modelClass) {
    final String builderClassName = this.getUniqueBuilderClassName();

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(
        "Storing model type `{0}` in cache for builder type `{1}`.",
        modelClass.getCanonicalName(),
        builderClassName);
    }

    getModelTypeCache().put(builderClassName, modelClass);
  }

  /**
   * Uses reflection to attempt to infer the model type by the generic signature or enclosing class
   * of the builder.
   *
   * @return  The type of model that this builder constructs.
   *
   * @throws  IllegalArgumentException
   *          If the model type cannot be determined by the way the builder was declared.
   */
  private Class<M> identifyModelClass()
  throws IllegalArgumentException {
    final Class<M>                 modelClass;
    final List<Supplier<Class<M>>> fetchStrategies;

    fetchStrategies = Arrays.asList(
      this::identifyModelTypeByGenericType,
      this::identifyModelTypeByEnclosingClass
    );

    modelClass =
      fetchStrategies.stream()
        .map(Supplier::get)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() ->
          new IllegalArgumentException(
            String.format(
              "The builder is expected either to have a generic parameter that is a sub-class of "
              + "`%s`, or it is expected to be declared as an inner class of the model it builds.",
              Model.class.getCanonicalName())));

    return modelClass;
  }

  /**
   * Attempts to determine the type of model that was passed into the generic type parameter
   * signature of the builder class at the time it was declared.
   *
   * <p>This requires that the builder be declared as a static, inner class -- with concrete types
   * provided for the model type generic parameter. Otherwise, Java normally uses Type erasure when
   * dealing with generic parameters.
   *
   * @return  The type of model type that was inferred from the generic type parameter on the
   *          builder; or, {@code null} if the type could not be inferred (typically because the
   *          builder uses additional, unbound type parameters to allow sub-classes to provide
   *          concrete types).
   */
  @SuppressWarnings("unchecked")
  private Class<M> identifyModelTypeByGenericType() {
    Class<M>    modelType        = null;
    final Type  currentClassType = this.getBuilderType().getGenericSuperclass();

    if (currentClassType instanceof ParameterizedType) {
      final ParameterizedType parameterizedCurrentClass;
      final Type              modelTypeParam;

      parameterizedCurrentClass = (ParameterizedType)currentClassType;

      modelTypeParam =
        Arrays.stream(parameterizedCurrentClass.getActualTypeArguments()).findFirst().orElse(null);

      if ((modelTypeParam instanceof Class)
          && (Model.class.isAssignableFrom((Class)modelTypeParam))) {
        modelType = (Class<M>)modelTypeParam;

        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace(
            "Resolved model type `{0}` for builder type `{1}` using annotation on builder.",
            modelType.getCanonicalName(),
            this.getUniqueBuilderClassName());
        }
      }
    }

    return modelType;
  }

  /**
   * Attempts to determine the type of model to create based on the class that encloses the builder.
   *
   * <p>By convention, all builders should be static inner classes of the models they build.
   *
   * @return  The type of model type that was inferred from the enclosing class of the builder; or,
   *          {@code null} if there is no enclosing class, or it is not a type of model.
   */
  @SuppressWarnings("unchecked")
  private Class<M> identifyModelTypeByEnclosingClass() {
    final Class<M>  modelType;
    final Class<?>  enclosingClass = this.getBuilderType().getEnclosingClass();

    if ((enclosingClass != null) && (Model.class.isAssignableFrom(enclosingClass))) {
      modelType = (Class<M>)enclosingClass;

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
          "Resolved model type `{0}` for builder type `{1}` using class that encloses builder.",
          modelType.getCanonicalName(),
          this.getUniqueBuilderClassName());
      }
    } else {
      modelType = null;
    }

    return modelType;
  }
}
