/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.construction;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.annotation.BuilderPopulatedField;
import com.rosieapp.services.common.model.fieldhandling.FieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.FieldValuePreprocessor;
import com.rosieapp.services.common.model.fieldhandling.StrictFieldDependencyHandler;
import com.rosieapp.services.common.model.filtering.ComparisonType;
import com.rosieapp.services.common.model.filtering.ReflectionBasedFilterBuilder;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.reflection.BuilderTypeReflector;
import com.rosieapp.services.common.model.reflection.ModelTypeReflector;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * An optional base class for model builders that wish to use annotations on fields to control
 * field population, to avoid having to declare fields in both the model and builder classes and
 * define constants.
 *
 * <p>The builder must be declared as a static inner class within the model that it builds.
 *
 * <p>Each field that the builder is expected to populate should be annotated with a
 * {@link BuilderPopulatedField} annotation. Any fields that are required for the object to be
 * constructed should have their {@code required} property on the annotation set to {@code true}.
 *
 * <p>In addition to being able to control which properties are required, the
 * {@code BuilderPopulatedField} annotation also provides control over which
 * {@link FieldValuePreprocessor} is used when values are being copied from the builder into the
 * new model instance. The pre-processor is not invoked when building filters, as they might
 * interfere with object equality during the filtering process.
 *
 * @param <M>
 *        The type of model that the builder builds.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 *
 * @see BuilderPopulatedField
 */
public abstract class AnnotationBasedModelBuilder<M extends Model,
                                                  B extends AnnotationBasedModelBuilder<M, B>>
extends MapBasedModelBuilder<M, B> {
  /**
   * This map of fields that this builder will populate on the model.
   */
  private Map<String, Field> targetFields;

  /**
   * The reflector used to obtain information about annotation-based builders and the model type
   * they are configured to produce.
   */
  private BuilderTypeReflector<M, B> builderTypeReflector;

  /**
   * The reflector used to obtain information about models and their fields.
   */
  private ModelTypeReflector<M> modelTypeReflector;

  /**
   * Default constructor for {@code AnnotationBasedModelBuilder}.
   *
   * <p>Initializes the model builder to strictly validate required fields.
   */
  protected AnnotationBasedModelBuilder() {
    this(new StrictFieldDependencyHandler());
  }

  /**
   * Constructor for {@code AnnotationBasedModelBuilder}.
   *
   * @param valueProvider
   *        A handler for controlling how optional and required fields are treated during object
   *        construction.
   */
  protected AnnotationBasedModelBuilder(final FieldDependencyHandler valueProvider) {
    super(valueProvider);

    this.createReflectors();
    this.populateTargetFields();
  }

  @Override
  public M build()
  throws IllegalStateException {
    final M                   model       = this.instantiateModelWithId();
    final Map<String, Field>  fieldValues = this.getTargetFields();

    for (final Entry<String, Field> fieldEntry : fieldValues.entrySet()) {
      final String  fieldName = fieldEntry.getKey();
      final Field   field     = fieldEntry.getValue();

      try {
        this.populateField(model, fieldName, field);
      } catch (IllegalAccessException | IllegalArgumentException ex) {
        throw new IllegalStateException(
          MessageFormat.format(
            "Could not populate the field `{0}` on model type `{1}`",
            fieldName,
            model.getClass().getCanonicalName()),
          ex);
      }
    }

    return model;
  }

  @Override
  public M buildShallow()
  throws IllegalStateException {
    if (this.getId() == null) {
      throw new IllegalStateException("`id` must be set prior to calling this method");
    }

    return this.instantiateModelWithId();
  }

  @Override
  public ReflectionBasedFilterBuilder<M, ?> toFilterBuilder() {
    final ReflectionBasedFilterBuilder<M, ?>  filterBuilder;
    final ModelIdentifier                     id            = this.getId();

    filterBuilder = this.createFilterBuilder(this.getTargetFields());

    if (id != null) {
      filterBuilder.withId(ComparisonType.EQUAL_TO, id);
    }

    for (final Entry<String, Field> fieldEntry : this.targetFields.entrySet()) {
      final String  fieldName;
      final Field   field;
      final Object  targetValue;

      fieldName   = fieldEntry.getKey();
      field       = fieldEntry.getValue();
      targetValue = this.getFieldValue(fieldName);

      if (targetValue != null) {
        filterBuilder.withFieldEqualTo(field, targetValue);
      }
    }

    return filterBuilder;
  }

  /**
   * {@inheritDoc}
   *
   * <p>The name string provided must exactly match the name of a field that has been annotated
   * {@link BuilderPopulatedField} in the model class. An exception will be thrown if no such field
   * is found.
   *
   * @throws  IllegalArgumentException
   *          If {@code fieldName} does not match the name of an annotated field on the model.
   */
  @Override
  protected <F> void putFieldValue(final String fieldName, final F value)
  throws IllegalArgumentException {
    this.validateFieldName(fieldName);

    super.putFieldValue(fieldName, value);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The name string provided must exactly match the name of a field that has been annotated
   * {@link BuilderPopulatedField} in the model class. An exception will be thrown if no such field
   * is found.
   *
   * @throws  IllegalArgumentException
   *          If {@code fieldName} does not match the name of an annotated field on the model.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected <F> F getFieldValue(final String fieldName)
  throws IllegalArgumentException {
    this.validateFieldName(fieldName);

    return super.getFieldValue(fieldName);
  }

  /**
   * Constructs a new filter builder to wrap the provided model builder field values.
   *
   * <p>This is an injection point for sub-classes to provide their own specific filter builder
   * types.
   *
   * @return  The new filter builder.
   */
  protected ReflectionBasedFilterBuilder<M, ?> createFilterBuilder(
                                                            final Map<String, Field> targetFields) {
    return new ReflectionBasedFilterBuilder<>(targetFields);
  }

  /**
   * Builds a new, shallow instance of the model, and then assigns it an ID.
   *
   * <p>Sub-classes can override this method if they have to perform special logic to instantiate
   * a model with an ID, or after a model has been instantiated with an ID.
   *
   * @return  The shallow model instances.
   */
  protected M instantiateModelWithId() {
    final M model = this.instantiateModel();

    model.assignId(this.buildId());

    return model;
  }

  /**
   * Builds a new instance of the model type via its private constructor.
   *
   * <p>Sub-classes can override this method if they have to perform special logic to instantiate
   * a model or after a model has been instantiated.
   *
   * @return  A new instance of the model this builder creates.
   */
  protected M instantiateModel() {
    final Class<? extends M>    modelClass = this.getModelClass();
    final ModelTypeReflector<M> reflector  = new ModelTypeReflector<>(modelClass);

    return reflector.instantiateModel();
  }

  /**
   * Obtains the type of model that this builder builds.
   *
   * <p>This is typically determined automatically by the builder by reflecting on the generic
   * signature and enclosing type of this builder class, based on how the builder class was
   * declared.
   *
   * <p>This requires that the builder be declared as a static, inner class -- with concrete types
   * provided for generic parameters. If generics are not bound to concrete types, the class that
   * encloses the builder is examined as a fallback, and is used only if it is a model type.
   * See {@link BuilderTypeReflector#getModelClass()} for more information on how this works.
   *
   * <p>Whenever possible, builder sub-classes should follow both of the conventions described
   * above. As a last resort, a sub-class may override this method to handle a special case that
   * causes it to deviate from these conventions.
   *
   * @return  The type of model that this builder creates.
   *
   * @throws  IllegalStateException
   *          If the model type cannot be inferred from the way that the builder was declared
   *          (typically because the builder is not following the standard conventions).
   */
  protected Class<? extends M> getModelClass() {
    return this.builderTypeReflector.getModelClass();
  }

  /**
   * Gets the list of fields that the builder is expected to populate.
   *
   * <p>The list is cached, for performance reasons.
   *
   * @return  The list of target fields.
   */
  private Map<String, Field> getTargetFields() {
    return this.targetFields;
  }

  /**
   * Creates the objects this builder uses to obtain information through the Java reflection API
   * about the builder and model classes at run-time.
   */
  @SuppressWarnings("unchecked")
  private void createReflectors() {
    // There is no way to truly satisfy the compiler that this type is safe, since it's a run-time
    // type so types can't be checked at compile time. We know it's safe, though, because the
    // builder has to extend from the AnnotationBasedModelBuilder base class.
    this.builderTypeReflector = new BuilderTypeReflector<>((Class<B>)this.getClass());

    this.modelTypeReflector = new ModelTypeReflector<>(this.getModelClass());
  }

  /**
   * Populates the map of field names to field objects.
   */
  private void populateTargetFields() {
    this.targetFields = this.modelTypeReflector.getAllBuilderPopulatedFields();
  }

  /**
   * Attempts to populate the specified field in the provided model object.
   *
   * <p>The value of the field is retrieved through the standard internal builder interface for
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
   *
   * @throws  IllegalArgumentException
   *          If the field is somehow mis-configured (e.g. it has a bad pre-processor), causing
   *          attempts to set its value to fail.
   */
  private void populateField(final M model, final String fieldName, final Field field)
  throws IllegalAccessException, IllegalArgumentException {
    final BuilderPopulatedField                   fieldAnnotation;
    final boolean                                 fieldIsRequired;
    final Class<? extends FieldValuePreprocessor> fieldPreprocessor;
    final Object                                  rawValue,
                                                  processedValue;

    fieldAnnotation = field.getAnnotation(BuilderPopulatedField.class);

    fieldIsRequired   = fieldAnnotation.required();
    fieldPreprocessor = fieldAnnotation.preprocessor();

    field.setAccessible(true);

    if (fieldIsRequired) {
      rawValue = this.getRequiredFieldValue(fieldName);
    } else {
      rawValue = this.getOptionalFieldValue(fieldName, field.get(model));
    }

    processedValue = this.invokePreprocessor(fieldPreprocessor, field, rawValue);

    field.set(model, processedValue);
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
  private void validateFieldName(final String fieldName)
  throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(fieldName, "fieldName cannot be null");

    if (!this.getTargetFields().containsKey(fieldName)) {
      throw new IllegalArgumentException(
        String.format(
          "No field within `%s` named `%s` and annotated with BuilderPopulatedField was found.",
          this.getModelClass().getCanonicalName(),
          fieldName));
    }
  }

  /**
   * Invokes the specified pre-processor on the provided value of the specified field.
   *
   * <p>If the field value or the pre-processor are provided as {@code null}, then the raw value is
   * passed through as-is, without pre-processing.
   *
   * @param   fieldPreprocessor
   *          The pre-processor to invoke.
   * @param   field
   *          The field on which the pre-processor is being invoked.
   * @param   rawFieldValue
   *          The raw value of the field, prior to pre-processing.
   *
   * @return  Either the pre-processed value; or the same value provided for {@code fieldValue} if
   *          either the field value or the pre-processor was {@code null}.
   *
   * @throws  IllegalArgumentException
   *          If the provided field pre-processor is improperly coded and cannot be instantiated.
   */
  protected <T> T invokePreprocessor(
                                  final Class<? extends FieldValuePreprocessor> fieldPreprocessor,
                                  final Field field,
                                  final T rawFieldValue)
  throws IllegalArgumentException {
    final T processedValue;

    // Do not bother with pre-processing if we don't have a pre-processor or we have no field value
    // to pre-process
    if ((rawFieldValue == null) || (fieldPreprocessor == null)) {
      processedValue = rawFieldValue;
    } else {
      try {
        processedValue = fieldPreprocessor.newInstance().preprocessField(field, rawFieldValue);
      } catch (IllegalAccessException | InstantiationException ex) {
        throw new IllegalArgumentException(
          MessageFormat.format(
            "Invalid field pre-processor provided -- `{0}` cannot be instantiated.",
            fieldPreprocessor.getCanonicalName()),
          ex);
      }
    }

    return processedValue;
  }
}
