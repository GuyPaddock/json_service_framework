package com.rosieapp.services.common.model.builder;

import com.rosieapp.common.collections.Maps;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.field.FieldValueHandler;
import com.rosieapp.services.common.model.field.ValidatingFieldHandler;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An optional base class for model builders that wish to stash the field values that will be used
 * to construct the model in a map, to avoid having to duplicate each field value definition.
 *
 * @param <M> {@inheritDoc}
 * @param <B> {@inheritDoc}
 */
public abstract class MapBasedModelBuilder<M extends Model, B extends MapBasedModelBuilder<M, B>>
extends AbstractModelBuilder<M, B> {
  /**
   * The map of values that will be used to construct the new model, keyed by the field name.
   */
  private final Map<String, Object> fieldValueMap;

  /**
   * Returns a string representation of this builder, including the values that have been stashed
   * for model construction so far.
   *
   * @return  The builder class name and the string representation of the field value map, in the
   *          format {@code ClassName{key1=value1, key2=value2}}.
   */
  @Override
  public String toString() {
    final String className = this.getClass().getCanonicalName(),
                 valueStr  = this.getFieldValuesAsString();

    return String.format("%s{%s}", className, valueStr);
  }

  /**
   * Default constructor for {@link MapBasedModelBuilder}.
   *
   * Initializes the model builder to strictly validate required fields.
   */
  protected MapBasedModelBuilder() {
    this(new ValidatingFieldHandler());
  }

  /**
   * Constructor for {@link MapBasedModelBuilder}.
   *
   * @param valueHandler
   *        A handler for controlling how optional and required fields are handled during object
   *        construction.
   */
  protected MapBasedModelBuilder(final FieldValueHandler valueHandler) {
    super(valueHandler);

    this.fieldValueMap = new HashMap<>();
  }

  /**
   * Stashes the value to use for the specified field when the model is constructed.
   * <p>
   * Sub-classes must ensure that the type of the object is correct for the type of field being
   * stashed, since all field values are stored in the same map.
   *
   * @param   fieldName
   *          The name of the field for which a value is being stashed.
   * @param   value
   *          The value to use for the field at construction time.
   *
   * @param   <F>
   *          The type of the field value.
   */
  protected <F> void putFieldValue(String fieldName, F value) {
    this.getFieldValueMap().put(fieldName, value);
  }

  /**
   * Gets the value that has been stashed for the specified field.
   *
   * @param   fieldName
   *          The name of the desired field.
   *
   * @param   <F>
   *          The expected type of the field value.
   *
   * @return  Either the value for the requested field; or, {@code null} if no value has been
   *          stashed for the field.
   */
  @SuppressWarnings("unchecked")
  protected <F> F getFieldValue(String fieldName) {
    return (F)this.getFieldValueMap().get(fieldName);
  }

  /**
   * Requests, optionally validates, and then returns the value to use when populating the specified
   * required field for a model being constructed by this builder.
   * <p>
   * The value of the field (if any value has been stashed) is automatically retrieved from the map
   * of stashed field values, and then the request is delegated to
   * {@link #getRequiredField(Object, String)}.
   *
   * @see     FieldValueHandler
   *
   * @param   fieldName
   *          The name of the field, which is used to retrieve the target field. It may also be used
   *          by the field value handler to construct an exception message if the field has no
   *          value.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field value handler, this will typically be a non-null value to use
   *          for the field, but may be {@code null} if the value handler is lax on validating
   *          that all required fields are populated.
   *
   * @throws  IllegalStateException
   *          If the required field value is {@code null} or invalid, and the field value handler
   *          considers this to be an error.
   */
  protected <F> F getRequiredField(final String fieldName)
  throws IllegalStateException {
    return this.getRequiredField(this.getFieldValue(fieldName), fieldName);
  }

  /**
   * Returns the value to use when populating the specified optional field for a model being
   * constructed by this builder.
   * <p>
   * The value of the field (if any value has been stashed) is automatically retrieved from the map
   * of stashed field values, and then the request is delegated to
   * {@link #getOptionalField(Object, Object)}.
   *
   * @see     FieldValueHandler
   *
   * @param   fieldName
   *          The name of the field, which is used to retrieve the target field.
   *
   * @param   defaultValue
   *          The default value that the builder would prefer to receive if a value for the field
   *          has not yet been stashed.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field value handler, this will typically be either the value of
   *          the requested field, or the default value if the field did not have a value.
   */
  @SuppressWarnings("unchecked")
  protected <F> F getOptionalField(final String fieldName, final F defaultValue) {
    return this.getOptionalField((F)this.getFieldValue(fieldName), defaultValue);
  }

  /**
   * Gets all of the field values that have been stashed in the map.
   *
   * @return  The map of field values.
   */
  private Map<String, Object> getFieldValueMap() {
    return this.fieldValueMap;
  }

  /**
   * Gets a string representation of all of the values in this builder.
   * <p>
   * The {@code id} that will be used for the new model is automatically pre-pended to the
   * output, to simplify debugging.
   *
   * @return  A string representation of all of the values that have been stashed so far for
   *          constructing models with this builder.
   */
  private String getFieldValuesAsString() {
    final String string;

    string =
      Maps.toString(
        Stream.concat(
          Stream.of(new SimpleEntry<>("id", this.buildId())),
          this.getFieldValueMap().entrySet().stream()
        ));

    return string;
  }
}
