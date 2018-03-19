package com.rosieapp.services.assertions;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.description.Description.mostRelevantDescription;
import static org.assertj.core.extractor.Extractors.byName;
import static org.assertj.core.extractor.Extractors.extractedDescriptionOf;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.construction.MapBasedModelBuilder;
import java.util.Map;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.util.introspection.IntrospectionError;

/**
 * Custom assertions for map-based model builders.
 *
 * @param <M>
 *        The type of model the builder constructs.
 * @param <B>
 *        The type of the builder.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class MapBasedModelBuilderAssert<M extends Model, B extends MapBasedModelBuilder<M, B>>
extends AbstractAssert<MapBasedModelBuilderAssert<M, B>, MapBasedModelBuilder<M, B>> {
  private static final String FIELD_MAP_FIELD_NAME = "fieldValueMap";

  /**
   * Constructor for {@code MapBasedModelBuilderAssert}.
   *
   * @param actual
   *        The model builder under test.
   */
  public MapBasedModelBuilderAssert(final MapBasedModelBuilder<M, B> actual) {
    super(actual, MapBasedModelBuilderAssert.class);
  }

  /**
   * Fluent entry point into this assertion class.
   *
   * <p>Use this with a static import of
   * {@code com.rosieapp.services.assertions.MapBasedModelBuilderAssert.assertThat}.
   *
   * @param   builder
   *          The builder under test.
   * @param   <M2>
   *          The type of model the builder constructs.
   * @param   <B2>
   *          The type of the builder.
   *
   * @return  The assertion matcher.
   */
  public static <M2 extends Model, B2 extends MapBasedModelBuilder<M2, B2>>
         MapBasedModelBuilderAssert<M2, B2> assertThat(final MapBasedModelBuilder<M2, B2> builder) {
    return new MapBasedModelBuilderAssert<>(builder);
  }

  /**
   * Extract the field map from the builder under test.
   *
   * <p>The field map becomes the map under test.
   *
   * @return  A new assertion object whose object under test is the field map of the builder.
   *
   * @throws  IntrospectionError
   *          If the field map is somehow missing from the builder.
   */
  @SuppressWarnings("unchecked")
  public MapAssert<String, Object> extractingFieldMap()
  throws IntrospectionError {
    final Map<String, Object> actualValue;
    final String              extractedMapDescription,
                              description;

    actualValue = (Map<String, Object>)byName(FIELD_MAP_FIELD_NAME).extract(this.actual);

    extractedMapDescription = extractedDescriptionOf(FIELD_MAP_FIELD_NAME);
    description             = mostRelevantDescription(info.description(), extractedMapDescription);

    return new MapAssert<>(actualValue).as(description);
  }

  /**
   * Asserts that the builder under test has a field with the given name and value.
   *
   * @param   name
   *          The name of the field.
   * @param   expectedValue
   *          The value expected for the field.
   *
   * @return  This object, for chaining builder calls.
   */
  public MapBasedModelBuilderAssert hasFieldValue(final String name, final Object expectedValue) {
    extractingFieldMap().contains(entry(name, expectedValue));

    return this;
  }

  /**
   * Extract a specific field from the field map of the builder under test.
   *
   * <p>The field becomes the object under test.
   *
   * @param   fieldName
   *          The name of the desired field.
   *
   * @param   <T>
   *          The type of field expected.
   *
   * @return  A new assertion object whose object under test is the extracted field.
   *
   * @throws  IntrospectionError
   *          If there is no field in the map with the specified field name.
   */
  @SuppressWarnings("unchecked")
  public <T> ObjectAssert<T> extractingField(final String fieldName) {
    final Object              fieldValue;
    final Map<String, Object> fieldMap;

    fieldMap    = (Map<String, Object>)byName(FIELD_MAP_FIELD_NAME).extract(this.actual);
    fieldValue  = fieldMap.get(fieldName);

    return new ObjectAssert<>((T)fieldValue);
  }

  /**
   * Extract a specific map field from the field map of the builder under test.
   *
   * <p>The extracted map becomes the object under test.
   *
   * @param   fieldName
   *          The name of the desired map field.
   *
   * @param   <K>
   *          The type of keys expected.
   * @param   <V>
   *          The type of values expected.
   *
   * @return  A new assertion object whose object under test is the extracted map field.
   *
   * @throws  IntrospectionError
   *          If there is no field in the map with the specified field name.
   */
  @SuppressWarnings("unchecked")
  public <K, V> MapAssert<K, V> extractingMapField(final String fieldName) {
    final Object              fieldValue;
    final Map<String, Object> fieldMap;

    fieldMap    = (Map<String, Object>)byName(FIELD_MAP_FIELD_NAME).extract(this.actual);
    fieldValue  = fieldMap.get(fieldName);

    Assertions.assertThat(fieldValue).isInstanceOf(Map.class);

    return new MapAssert<>((Map<K, V>)fieldValue);
  }

  /**
   * Extract the value of a specific key from a specific map field in the field map of the builder
   * under test.
   *
   * <p>The extracted value becomes the object under test.
   *
   * @param   fieldName
   *          The name of the map field.
   * @param   valueKey
   *          The key of the desired value in the map field.
   *
   * @param   <T>
   *          The type of value expected.
   *
   * @return  A new assertion object whose object under test is the value extracted from the map
   *          field.
   *
   * @throws  IntrospectionError
   *          If there is no field in the map with the specified field name.
   */
  @SuppressWarnings("unchecked")
  public <T> ObjectAssert<T> extractingValueOfMapField(final String fieldName,
                                                       final Object valueKey) {
    final Object              fieldValue;
    final Map<String, Object> fieldMap;
    final Map<Object, Object> mapValue;

    fieldMap    = (Map<String, Object>)byName(FIELD_MAP_FIELD_NAME).extract(this.actual);
    fieldValue  = fieldMap.get(fieldName);

    Assertions.assertThat(fieldValue).isInstanceOf(Map.class);

    mapValue = (Map<Object, Object>)fieldValue;

    return new ObjectAssert<>((T)mapValue.get(valueKey));
  }
}
