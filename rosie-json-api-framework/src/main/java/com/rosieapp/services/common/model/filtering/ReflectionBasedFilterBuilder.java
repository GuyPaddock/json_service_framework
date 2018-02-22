/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.filtering.criteria.ReferencedModelCriterion;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A filter builder that relies on reflection for obtaining the model values it uses for comparison.
 *
 * @param <M>
 *        The type of model for which a filter is being constructed.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 */
public class ReflectionBasedFilterBuilder<M extends Model, B extends ReflectionBasedFilterBuilder<M, B>>
extends CriteriaBasedFilterBuilder<M, B> {
  final Map<String, Field> fieldMap;

  /**
   * Constructor for {@code ReflectionBasedFilterBuilder}.
   *
   * <p>Initializes a new reflection-based builder with the provided map of fields. This map is
   * typically provided by a corresponding
   * {@link com.rosieapp.services.common.model.construction.MapBasedModelBuilder} instance that has
   * built up its own map of fields using its intimate level of knowledge about the model it builds.
   *
   * @param fieldMap
   *        The map from field names to reflection field objects.
   */
  public ReflectionBasedFilterBuilder(final Map<String, Field> fieldMap) {
    super();

    this.fieldMap = Collections.unmodifiableMap(new HashMap<>(fieldMap));
  }

  //================================================================================================
  // ID Comparison
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the model's
   * identifier against the provided identifier using the specified comparison type.
   *
   * @param   comparisonType
   *          The type of comparison being done between the the model's identifier and the
   *          {@code targetId}.
   * @param   targetId
   *          The ID against which the model's identifier will be compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withId(final ComparisonType comparisonType, final ModelIdentifier targetId) {
    return this.withCriterion(comparisonType.buildFor(Model::getId, targetId));
  }

  //================================================================================================
  // Less Than
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the specified
   * field against the specified target value, and only proceed if the model's value is less than
   * the target value.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldLessThan(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.LESS_THAN, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will compare the provided
   * field against the specified target value, and only proceed if the model's value is less than
   * the target value.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldLessThan(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.LESS_THAN, targetValue);
  }

  //================================================================================================
  // Less Than or Equal To
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the specified
   * field against the specified target value, and only proceed if the model's value is less than
   * or equal to the target value.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldLessThanOrEqualTo(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.LESS_THAN_OR_EQUAL, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will compare the provided
   * field against the specified target value, and only proceed if the model's value is less than or
   * equal to the target value.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldLessThanOrEqualTo(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.LESS_THAN_OR_EQUAL, targetValue);
  }

  //================================================================================================
  // Equal To
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the specified
   * field against the specified target value, and only proceed if the model's value is equal to the
   * target value.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldEqualTo(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.EQUAL_TO, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will compare the provided
   * field against the specified target value, and only proceed if the model's value is equal to the
   * target value.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldEqualTo(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.EQUAL_TO, targetValue);
  }

  //================================================================================================
  // Greater Than or Equal To
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the specified
   * field against the specified target value, and only proceed if the model's value is greater than
   * or equal to the target value.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldGreaterThanOrEqualTo(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.GREATER_THAN_OR_EQUAL, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will compare the provided
   * field against the specified target value, and only proceed if the model's value is greater than
   * or equal to the target value.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldGreaterThanOrEqualTo(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.GREATER_THAN_OR_EQUAL, targetValue);
  }

  //================================================================================================
  // Greater Than
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will compare the specified
   * field against the specified target value, and only proceed if the model's value is greater than
   * the target value.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldGreaterThan(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.GREATER_THAN, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will compare the provided
   * field against the specified target value, and only proceed if the model's value is greater than
   * the target value.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldGreaterThan(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.GREATER_THAN, targetValue);
  }

  //================================================================================================
  // Contains
  //================================================================================================
  /**
   * Add a criterion to the next filter built, such that the filter will determine if the specified
   * value is contained within the value of the specified field, and only proceed if it is.
   *
   * @param   fieldName
   *          The name of the model field being compared against the target.
   * @param   targetValue
   *          The value being searched for inside the model value.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withFieldContaining(final String fieldName, final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(fieldName, ComparisonType.CONTAINS, targetValue);
  }

  /**
   * Add a criterion to the next filter built, such that the filter will determine if the specified
   * value is contained within the value of the provided field, and only proceed if it is.
   *
   * @param   field
   *          The model field being compared against the target.
   * @param   targetValue
   *          The value against which the model value is being compared.
   *
   * @return  This object, for chaining builder calls.
   */
  public B withFieldContaining(final Field field, final Object targetValue) {
    return this.withCriterionForField(field, ComparisonType.CONTAINS, targetValue);
  }

  //================================================================================================
  // Sub-model Matching
  //================================================================================================
  /**
   * Adds a criterion to this filter for matching the provided model filter against the model
   * reference field having the specified name.
   *
   * <p>The field must be one that was registered in the map that was provided to this builder when
   * it was created, or an exception will be raised.
   *
   * @param   fieldName
   *          The name of the target field.
   * @param   modelFilter
   *          The filter to evaluate on the model that the field references.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public <S extends Model> B withCriterionForSubModel(final String fieldName,
                                                      final ModelFilter<S> modelFilter)
  throws IllegalArgumentException {
    return this.withCriterionForSubModel(this.getField(fieldName), modelFilter);
  }

  /**
   * Adds a criterion to this filter for matching the provided model filter against the provided
   * model reference field.
   *
   * @param   referencedField
   *          The target field.
   * @param   modelFilter
   *          The filter to evaluate on the model that the field references.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public <S extends Model> B withCriterionForSubModel(final Field referencedField,
                                                      final ModelFilter<S> modelFilter)
  throws IllegalArgumentException {
    return this.withCriterion(new ReferencedModelCriterion<>(referencedField, modelFilter));
  }

  //================================================================================================
  // Utility Methods
  //================================================================================================
  /**
   * Adds a criterion to this filter for matching values against the field with the specified name.
   *
   * <p>The field must be one that was registered in the map that was provided to this builder when
   * it was created, or an exception will be raised.
   *
   * @param   fieldName
   *          The name of the target field.
   * @param   comparisonType
   *          The type of comparison being done between the field and the target value.
   * @param   targetValue
   *          The value against which the field will be compared.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  public B withCriterionForField(final String fieldName, final ComparisonType comparisonType,
                                 final Object targetValue)
  throws IllegalArgumentException {
    return this.withCriterionForField(this.getField(fieldName), comparisonType, targetValue);
  }

  /**
   * Adds a criterion to this filter for matching values against the provided field of the model.
   *
   * @param   field
   *          The target field.
   * @param   comparisonType
   *          The type of comparison being done between the field and the target value.
   * @param   targetValue
   *          The value against which the field will be compared.
   *
   * @return  This object, for chaining builder calls.
   */
  @SuppressWarnings("unchecked")
  public B withCriterionForField(final Field field, final ComparisonType comparisonType,
                                 final Object targetValue) {
    this.withCriterion(comparisonType.buildFor(field, targetValue));

    return (B)this;
  }

  /**
   * Ensures that the provided field value is valid, and then returns it from the field map.
   *
   * @param   name
   *          The name of the desired field from the map.
   *
   * @return  The desired field.
   *
   * @throws  IllegalArgumentException
   *          If there is no field in the field map of this builder that has the specified name.
   */
  protected Field getField(final String name)
  throws IllegalArgumentException {
    final Field field = this.fieldMap.get(name);

    if (field == null) {
      throw new IllegalArgumentException(
        MessageFormat.format("A field named `{0}` is not known to this builder.", name));
    }

    return field;
  }
}
