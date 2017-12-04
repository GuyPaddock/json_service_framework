package com.rosieapp.services.common.model.construction;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.services.common.model.fieldhandling.FieldValueProvider;
import com.rosieapp.services.common.model.fieldhandling.StrictFieldProvider;
import com.rosieapp.services.common.model.filtering.ModelFilter;
import com.rosieapp.services.common.model.filtering.ReflectionBasedModelFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An optional base class for model builders that wish to use annotations on fields to control
 * field population, to avoid having to declare fields in both the model and builder classes and
 * define constants.
 * <p>
 * The builder must be declared as a static inner class within the model that it builds.
 * <p>
 * Each field that the builder is expected to populate should be annotated with a
 * {@link BuilderPopulatedField} annotation. Any fields that are required for the object to be
 * constructed should have their {@code required} property on the annotation set to {@code true}.
 * <p>
 * In addition to being able to control which properties are required, the
 * {@code BuilderPopulatedField} annotation also provides control over which
 * {@link FieldValuePreprocessor} is used when values are being copied from the builder into the
 * new model instance. The pre-processor is not invoked when building filters, as they might
 * interfere with object equality during the filtering process.
 *
 * @param <M> {@inheritDoc}
 * @param <B> {@inheritDoc}
 *
 * @see BuilderPopulatedField
 */
public abstract class AnnotationBasedModelBuilder<M extends Model,
                                                  B extends AnnotationBasedModelBuilder<M, B>>
extends MapBasedModelBuilder<M, B> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationBasedModelBuilder.class);

  /**
   * A cache that increases the performance of looking up what fields to populate for a given model
   * type.
   * <p>
   * The cache is configured to store fields for no more than 32 model classes at a time, and evicts
   * entries after 10 minutes of use. This helps to ensure high throughput on payloads that are
   * processing the same types of models over and over.
   */
  private static final Cache<String, List<Field>> modelTypeToFieldsCache;

  /**
   * A cache that increases the performance of looking up what type of model to create for each
   * type of builder.
   * <p>
   * The cache is configured to store no more than 32 model classes at a time, and evicts entries
   * after 10 minutes of use. This helps to ensure high throughput on payloads that are processing
   * the same types of models over and over.
   */
  private static final Cache<String, Class<? extends Model>> builderToModelClassCache;

  static {
    final CacheBuilder<Object, Object> cacheBuilder =
      CacheBuilder.newBuilder()
        .maximumSize(32)
        .expireAfterWrite(10, TimeUnit.MINUTES);

    modelTypeToFieldsCache  = cacheBuilder.build();
    builderToModelClassCache = cacheBuilder.build();
  }

  /**
   * This map of fields that this builder will populate on the model.
   */
  private Map<String, Field> targetFields;

  /**
   * Default constructor for {@link AnnotationBasedModelBuilder}.
   *
   * Initializes the model builder to strictly validate required fields.
   */
  protected AnnotationBasedModelBuilder() {
    this(new StrictFieldProvider());
  }

  /**
   * Constructor for {@link AnnotationBasedModelBuilder}.
   *
   * @param valueProvider
   *        A provider for controlling how optional and required fields are handled during object
   *        construction.
   */
  protected AnnotationBasedModelBuilder(final FieldValueProvider valueProvider) {
    super(valueProvider);

    this.populateTargetFields();
  }

  @Override
  public M build()
  throws IllegalStateException {
    final M                   model         = this.instantiateModelWithId();
    final Map<String, Field>  targetFields  = this.getTargetFields();

    for (final Entry<String, Field> fieldEntry : targetFields.entrySet()) {
      final String  fieldName = fieldEntry.getKey();
      final Field   field     = fieldEntry.getValue();

      try {
        this.populateField(model, fieldName, field);
      } catch (IllegalAccessException ex) {
        throw new IllegalStateException(
          String.format(
            "Could not populate the field `%s` on model type `%s`: %s",
            fieldName,
            model.getClass().getName(),
            ex.getMessage()),
          ex);
      }
    }

    return model;
  }

  @Override
  public M buildShallow() throws IllegalStateException {
    if (this.getId() == null) {
      throw new IllegalStateException("`id` must be set prior to calling this method");
    }

    return this.instantiateModelWithId();
  }

  @Override
  public ModelFilter<M> buildFilter() {
    final ReflectionBasedModelFilter<M> filter        = new ReflectionBasedModelFilter<>();
    final Map<String, Field>            targetFields  = this.getTargetFields();

    for (final Entry<String, Field> fieldEntry : targetFields.entrySet()) {
      final String fieldName  = fieldEntry.getKey();
      final Field  field      = fieldEntry.getValue();
      final Object fieldValue = this.getFieldValue(fieldName);

      if (fieldValue != null) {
        filter.addCriterion(field, fieldValue);
      }
    }

    return filter;
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * The name string provided must exactly match the name of a field that has been annotated
   * {@link BuilderPopulatedField} in the model class. An exception will be thrown if no such field
   * is found.
   *
   * @throws  IllegalArgumentException
   *          If {@code fieldName} does not match the name of an annotated field on the model.
   */
  @Override
  protected <F> void putFieldValue(String fieldName, F value)
  throws IllegalArgumentException {
    this.validateFieldName(fieldName);

    super.putFieldValue(fieldName, value);
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * The name string provided must exactly match the name of a field that has been annotated
   * {@link BuilderPopulatedField} in the model class. An exception will be thrown if no such field
   * is found.
   *
   * @throws  IllegalArgumentException
   *          If {@code fieldName} does not match the name of an annotated field on the model.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected <F> F getFieldValue(String fieldName)
  throws IllegalArgumentException {
    this.validateFieldName(fieldName);

    return super.getFieldValue(fieldName);
  }

  /**
   * Gets the list of fields that the builder is expected to populate.
   * <p>
   * Only fields declared directly in the model class are returned. Fields declared in parent
   * classes of the model class are not currently supported.
   * <p>
   * The list is cached, for performance reasons.
   *
   * @return  The list of target fields.
   */
  private Map<String, Field> getTargetFields() {
    return this.targetFields;
  }

  /**
   * Builds a new, shallow instance of the model, and then assigns it an ID.
   *
   * @return  The shallow model instances.
   */
  private M instantiateModelWithId() {
    final M model = this.instantiateModel();

    model.assignId(this.buildId());

    return model;
  }

  /**
   * Invokes the private constructor for the model via reflection.
   *
   * @return  A new instance of the model this builder creates.
   */
  private M instantiateModel() {
    final M                  model;
    final Class<? extends M> modelClass = this.getModelClass();

    try {
      final Constructor<? extends M> constructor = modelClass.getDeclaredConstructor();

      constructor.setAccessible(true);

      model = constructor.newInstance();
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException(
        "Could not instantiate an instance of the model: " + ex.getMessage(), ex);
    }

    return model;
  }

  /**
   * Populates the map of field names to field objects.
   */
  private void populateTargetFields() {
    Map<String, Field>  targetFields;
    final List<Field>   allFields  = getAllTargetFields();

    targetFields =
      allFields
        .stream()
        .filter((field) -> field.isAnnotationPresent(BuilderPopulatedField.class))
        .collect(Collectors.toMap(Field::getName, Function.identity()));

    this.targetFields = targetFields;
  }

  /**
   * Gets all of the builder-populated fields in the target class and all of its parent classes.
   *
   * @return  A list of all of the fields that the builder must populate.
   */
  private List<Field> getAllTargetFields() {
    Class<? extends Model>  modelClass  = this.getModelClass();
    List<Field>             fields      = this.getCachedTargetFields(modelClass);

    if (fields == null) {
      fields = this.identifyTargetFields(modelClass);

      modelTypeToFieldsCache.put(modelClass.getCanonicalName(), fields);
    }

    return fields;
  }

  /**
   * Attempts to re-use a cached look-up of target fields for the specified model, if it exists.
   *
   * @param   modelClass
   *          The type of model for which fields are needed.
   *
   * @return  The target fields in the specified class.
   */
  private List<Field> getCachedTargetFields(final Class<? extends Model> modelClass) {
    final List<Field> fields;

    fields = modelTypeToFieldsCache.getIfPresent(modelClass.getCanonicalName());

    if (fields != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Resolved fields for model type `{}` using cache.", modelClass.getName());
      }
    }

    return fields;
  }

  /**
   * Identifies the builder-populated fields in the target class and all of its parent classes.
   *
   * @param   modelClass
   *          The type of model for which fields are needed.
   *
   * @return  A list of all of the fields that the builder must populate.
   */
  private List<Field> identifyTargetFields(final Class<? extends Model> modelClass) {
    final List<Field> result        = new LinkedList<>();
    Class<?>          currentClass  = modelClass;

    while (currentClass != null) {
      final Class<?> superClass = currentClass.getSuperclass();

      result.addAll(Arrays.asList(currentClass.getDeclaredFields()));

      if (Model.class.isAssignableFrom(superClass)) {
        currentClass = superClass;
      }
      else {
        currentClass = null;
      }
    }

    return result;
  }

  /**
   * Attempts to populate the specified field in the provided model object.
   *
   * The value of the field is retrieved through the standard internal builder interface for
   * required & optional fields.
   *
   * @param   model
   *          The model in which the field will be populated.
   * @param   fieldName
   *          The name of the field, as it has been declared in the class.
   * @param   field
   *          A reference to the field itself.
   *
   * @throws  IllegalAccessException
   *          If this builder does not have the required level of access to the model in order to
   *          populate the field.
   */
  private void populateField(M model, String fieldName, Field field)
  throws IllegalAccessException {
    final boolean fieldRequired = field.getAnnotation(BuilderPopulatedField.class).required();
    final Object  fieldValue;

    field.setAccessible(true);

    if (fieldRequired) {
      fieldValue = this.getRequiredField(fieldName);
    }
    else {
      fieldValue = this.getOptionalField(fieldName, field.get(model));
    }

    field.set(model, fieldValue);
  }

  /**
   * Ensures that the specified field is defined in the model and has the expected annotation.
   *
   * @param   fieldName
   *          The name of the field to verify exists.
   *
   * @throws  NullPointerException
   *          If {@code fieldName} is {@code null}.
   * @throws  IllegalArgumentException
   *          If there was no field that was within the model class with the required annotation.
   */
  private void validateFieldName(String fieldName)
  throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(fieldName, "fieldName must not be null");

    if (!this.getTargetFields().containsKey(fieldName)) {
      throw new IllegalArgumentException(
        String.format(
          "No field within `%s` named `%s` and annotated with BuilderPopulatedField was found.",
          this.getModelClass().getName(),
          fieldName));
    }
  }

  /**
   * Obtains the type of model that should be assembled by reflecting on the generic signature
   * and enclosing type of this builder class, at the time it was declared.
   * <p>
   * This requires that the builder be declared as a static, inner class -- with concrete types
   * provided for generic parameters. If generics are not bound to concrete types, the class that
   * encloses the builder is examined as a fallback, and is used only if it is a model type.
   *
   * @return  The type of model that this builder creates.
   *
   * @throws  IllegalStateException
   *          If this builder is not declared as a static inner class of the model, with a concrete
   *          model type.
   */
  @SuppressWarnings("unchecked")
  private Class<? extends M> getModelClass()
  throws IllegalStateException {
    final Class<? extends M>            modelClass;
    List<Supplier<Class<? extends M>>>  fetchStrategies = Arrays.asList(
      this::determineModelTypeUsingCache,
      this::determineModelTypeByGenericType,
      this::determineModelTypeByEnclosingClass
    );

    modelClass =
      fetchStrategies.stream()
        .map(Supplier::get)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() ->
          new IllegalStateException(
            String.format(
              "The builder is expected either to have a generic parameter that is a sub-class of " +
              "`%s`, or it is expected to be declared as an inner class of the model it builds.",
              Model.class.getName())));

    builderToModelClassCache.put(this.getClass().getName(), modelClass);

    return modelClass;
  }

  /**
   * Attempts to return the cached the model type for this builder, if we have already looked it
   * up in either this builder instance or another instance of the same builder.
   *
   * @return  Either the cached model type, if it has previously been resolved; or, {@code null} if
   *          it is not cached.
   */
  @SuppressWarnings("unchecked")
  private Class<? extends M> determineModelTypeUsingCache() {
    final Class<?>            builderClass = this.getClass();
    final Class<? extends M>  modelType;

    modelType = (Class<? extends M>)builderToModelClassCache.getIfPresent(builderClass.getName());

    if (modelType != null) {

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
          "Resolved model type `{0}` for builder type `{}` using cache.",
          modelType.getName(),
          builderClass.getName());
      }
    }

    return modelType;
  }

  /**
   * Attempts to determine the type of model that was passed into the generic signature of this
   * builder class at the time it was declared.
   * <p>
   * This requires that the builder be declared as a static, inner class -- with concrete types
   * provided for the model type generic parameter. Otherwise, Java normally uses Type erasure when
   * dealing with generic parameters.
   *
   * @return  The type of model type that was inferred from the generic type parameter on the
   *          builder; or, {@code null} if the type could not be inferred (typically because the
   *          builder uses additional, unbound type parameters to allow sub-classes to provide
   *          concrete types).
   */
  @SuppressWarnings("unchecked")
  private Class<? extends M> determineModelTypeByGenericType() {
    Class<? extends M> modelType        = null;
    final Class<?>     builderClass     = this.getClass();
    final Type         currentClassType = builderClass.getGenericSuperclass();

    if (currentClassType instanceof ParameterizedType) {
      final ParameterizedType parameterizedCurrentClass;
      final Type              modelTypeParam;

      parameterizedCurrentClass = (ParameterizedType)currentClassType;

      modelTypeParam =
        Arrays.stream(parameterizedCurrentClass.getActualTypeArguments()).findFirst().orElse(null);

      if ((modelTypeParam != null) && (modelTypeParam instanceof Class) &&
          (Model.class.isAssignableFrom((Class)modelTypeParam))) {
        modelType = (Class<? extends M>)modelTypeParam;

        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace(
            "Resolved model type `{}` for builder type `{}` using annotation on builder.",
            modelType.getName(),
            builderClass.getName());
        }
      }
    }

    return modelType;
  }

  /**
   * Attempts to determine the type of model to create based on the class that encloses the builder.
   * <p>
   * By convention, all builders should be static inner classes of the models they build.
   *
   * @return  The type of model type that was inferred from the enclosing class of the builder; or,
   *          {@code null} if there is no enclosing class, or it is not a type of model.
   */
  @SuppressWarnings("unchecked")
  private Class<? extends M> determineModelTypeByEnclosingClass() {
    final Class<? extends M>  modelType;
    final Class<?>            builderClass   = this.getClass(),
                              enclosingClass = builderClass.getEnclosingClass();

    if ((enclosingClass != null) && (Model.class.isAssignableFrom(enclosingClass))) {
      modelType = (Class<? extends M>)enclosingClass;

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
          "Resolved model type `{}` for builder type `{}` using class that encloses builder.",
          modelType.getName(),
          builderClass.getName());
      }
    }
    else {
      modelType = null;
    }

    return modelType;
  }
}
