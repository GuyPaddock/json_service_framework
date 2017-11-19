package com.rosieapp.services.common.model.builder;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.services.common.model.field.FieldValueHandler;
import com.rosieapp.services.common.model.field.ValidatingFieldHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An optional base class for model builders that wish to use annotations on fields to control
 * field population, to avoid having to declare fields in both the model and builder classes and
 * define constants.
 * <p>
 * The builder must be declared as a static inner class within the model that it builds.
 *
 * @param <M> {@inheritDoc}
 * @param <B> {@inheritDoc}
 */
public abstract class AnnotationBasedModelBuilder<M extends Model,
                                                  B extends AnnotationBasedModelBuilder<M, B>>
extends MapBasedModelBuilder<M, B> {
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
    this(new ValidatingFieldHandler());
  }

  /**
   * Constructor for {@link AnnotationBasedModelBuilder}.
   *
   * @param valueHandler
   *        A handler for controlling how optional and required fields are handled during object
   *        construction.
   */
  protected AnnotationBasedModelBuilder(final FieldValueHandler valueHandler) {
    super(valueHandler);

    this.populateTargetFields();
  }

  @Override
  public M build() throws IllegalStateException {
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
    Map<String, Field>       targetFields;
    final Class<? extends M> modelClass = this.getModelClass();
    final Field[]            allFields  = modelClass.getDeclaredFields();

    targetFields =
      Arrays
        .stream(allFields)
        .filter((field) -> field.isAnnotationPresent(BuilderPopulatedField.class))
        .collect(Collectors.toMap(Field::getName, Function.identity()));

    this.targetFields = targetFields;
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
   *          The name of the field to verify exists.
   *
   * @throws  IllegalArgumentException
   *          If there was no field that was within the model class with the required annotation.
   */
  private void validateFieldName(String fieldName) {
    if (!this.getTargetFields().containsKey(fieldName)) {
      throw new IllegalArgumentException(
        String.format(
          "No field within `%s` named `%s` and annotated with BuilderPopulatedField was found.",
          this.getModelClass().getName(),
          fieldName));
    }
  }

  /**
   * Obtains the type of model that was passed into the generic signature of this builder class
   * at the time it was declared.
   * <p>
   * This requires that the builder be declared as a static, inner class -- with both of the
   * expected generic parameters. Otherwise, Java normally uses Type erasure when dealing with
   * generic parameters.
   *
   * @return  The type of model that this builder creates.
   *
   * @throws  IllegalStateException
   *          If this builder is not declared as a static inner class with two type parameters.
   */
  @SuppressWarnings("unchecked")
  private Class<? extends M> getModelClass()
  throws IllegalStateException {
    final Class<? extends M> modelType;
    final Type               currentClassType = this.getClass().getGenericSuperclass();

    if (!(currentClassType instanceof ParameterizedType)) {
      throw new IllegalStateException(
        "The builder must be declared as a parameterized, inner class of the model object.");
    }
    else {
      final ParameterizedType parameterizedCurrentClass;
      final Type[]            typeParams;

      parameterizedCurrentClass = (ParameterizedType)currentClassType;
      typeParams                = parameterizedCurrentClass.getActualTypeArguments();

      if (typeParams.length != 2) {
        throw new IllegalStateException("The builder is expected to have two generic parameters.");
      }

      modelType = (Class<? extends M>)typeParams[0];
    }

    return modelType;
  }
}
