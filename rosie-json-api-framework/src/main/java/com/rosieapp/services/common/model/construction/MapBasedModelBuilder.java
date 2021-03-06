/*
 * Copyright (c) 2017-2018 Rosie Applications Inc.
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

package com.rosieapp.services.common.model.construction;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.fieldhandling.FieldDependencyHandler;
import com.rosieapp.services.common.model.fieldhandling.StrictFieldDependencyHandler;
import com.rosieapp.util.Maps;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An optional base class for model builders that wish to stash the field values that will be used
 * to construct the model in a map, to avoid having to duplicate each field value definition.
 *
 * @param <M>
 *        The type of model that the builder builds.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 */
public abstract class MapBasedModelBuilder<M extends Model,
                                           B extends MapBasedModelBuilder<M, B>>
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
   * <p>Initializes the model builder to strictly validate required fields.
   */
  protected MapBasedModelBuilder() {
    this(new StrictFieldDependencyHandler());
  }

  /**
   * Constructor for {@link MapBasedModelBuilder}.
   *
   * @param dependencyHandler
   *        A handler for controlling how optional and required fields are treated during object
   *        construction.
   */
  protected MapBasedModelBuilder(final FieldDependencyHandler dependencyHandler) {
    super(dependencyHandler);

    this.fieldValueMap = new LinkedHashMap<>();
  }

  /**
   * Stashes the value to use for the specified field when the model is constructed.
   *
   * <p>Sub-classes must ensure that the type of the object is correct for the type of field being
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
  protected <F> void putFieldValue(final String fieldName, final F value) {
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
  protected <F> F getFieldValue(final String fieldName) {
    return (F)this.getFieldValueMap().get(fieldName);
  }

  /**
   * Requests, optionally validates, and then returns the value to use when populating the specified
   * required field for a model being constructed by this builder.
   *
   * <p>The value of the field (if any value has been stashed) is automatically retrieved from the
   * map of stashed field values, and then the request is delegated to
   * {@link #supplyRequiredFieldValue(Object, String)}.
   *
   * @param   fieldName
   *          The name of the field, which is used to retrieve the target field. It may also be used
   *          by the field dependency handler to construct an exception message if the field has no
   *          value.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field dependency handler, this will typically be a non-null value to
   *          use for the field, but may be {@code null} if the handler is lax on validating that
   *          all required fields are populated.
   *
   * @throws  IllegalStateException
   *          If the required field value is {@code null} or invalid, and the field dependency
   *          handler considers this to be an error.
   *
   * @see     FieldDependencyHandler
   */
  protected <F> F getRequiredFieldValue(final String fieldName)
  throws IllegalStateException {
    return this.supplyRequiredFieldValue(this.getFieldValue(fieldName), fieldName);
  }

  /**
   * Returns the value to use when populating the specified optional field for a model being
   * constructed by this builder.
   *
   * <p>The value of the field (if any value has been stashed) is automatically retrieved from the
   * map of stashed field values, and then the request is delegated to
   * {@link #supplyOptionalFieldValue(Object, String, Object)}.
   *
   * @see     FieldDependencyHandler
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
   * @return  Depending on the field dependency handler, this will typically be either the value of
   *          the requested field, or the default value if the field did not have a value.
   */
  @SuppressWarnings("unchecked")
  protected <F> F getOptionalFieldValue(final String fieldName, final F defaultValue) {
    return this.supplyOptionalFieldValue(this.getFieldValue(fieldName), fieldName, defaultValue);
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
   *
   * <p>The {@code id} that will be used for the new model is automatically prepended to the
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
