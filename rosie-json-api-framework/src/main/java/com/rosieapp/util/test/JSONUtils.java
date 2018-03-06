/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.util.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.SerializationFeature;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.rosieapp.services.common.model.Model;
/**
 * Several convenient utility methods, for working with JSON in service model tests.
 */
public final class JSONUtils {
  /**
   * Private constructor for singleton utility class.
   */
  private JSONUtils() {
  }

  /**
   * Creates a JSON resource converter suitable for use in tests.
   *
   * <p>The resource converter is configured as follows:
   * <ul>
   *   <li>Unknown / unmapped properties are ignored during deserialization.</li>
   * </ul>
   *
   * @param   modelTypes
   *          The types of models that the resource converter should be configured to handle.
   *
   * @return  The new resource converter.
   */
  @SafeVarargs
  public static ResourceConverter createResourceConverterFor(
                                                       final Class<? extends Model>... modelTypes) {
    final ResourceConverter converter;
    final ObjectMapper      mapper = new ObjectMapper();

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(
      com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
      false
    );

    converter = new ResourceConverter(mapper, modelTypes);

    return converter;
  }

  /**
   * Creates a JSON resource converter suitable for use in tests.
   *
   * <p>The resource converter is configured as follows:
   * <ul>
   *   <li>All relationships are automatically included in the {@code included} portion of each JSON
   *       API response.</li>
   *   <li>Unknown / unmapped properties are ignored during deserialization.</li>
   * </ul>
   *
   * @param   modelTypes
   *          The types of models that the resource converter should be configured to handle.
   *
   * @return  The new resource converter.
   */
  @SafeVarargs
  public static ResourceConverter createResourceConverterThatIncludesRelationshipsFor(
                                                       final Class<? extends Model>... modelTypes) {
    final ResourceConverter converter = JSONUtils.createResourceConverterFor(modelTypes);

    converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);

    return converter;
  }

  /**
   * Converts the provided model to a string of JSON-API-compliant JSON.
   *
   * <p>A resource converter is constructed on-the-fly to handle the operation.
   *
   * @param   model
   *          The model to convert to JSON.
   *
   * @return  A representation of the model, as JSON.
   *
   * @throws  DocumentSerializationException
   *          If an issue with the model or its data prevents creating a JSON representation of the
   *          model.
   */
  public static String toJsonString(final Model model)
  throws DocumentSerializationException {
    return JSONUtils.toJsonString(model, JSONUtils.createResourceConverterFor(model.getClass()));
  }

  /**
   * Converts the provided model to a string of JSON-API-compliant JSON using the provided JSON
   * resource converter.
   *
   * @param   model
   *          The model to convert to JSON.
   * @param   converter
   *          The resource converter to use for the operation.
   *
   * @return  A representation of the model, as JSON.
   *
   * @throws  DocumentSerializationException
   *          If an issue with the model or its data prevents creating a JSON representation of the
   *          model.
   */
  public static String toJsonString(final Model model, final ResourceConverter converter)
  throws DocumentSerializationException {
    final JSONAPIDocument<Model> document;

    document = new JSONAPIDocument<>(model);

    return JSONUtils.toJsonString(document, converter);
  }

  /**
   * Converts the provided JSON API document to a string of JSON using the provided resource
   * converter.
   *
   * <p>This is useful for performing lower-level introspection of a JSON API response (i.e. dumping
   * a JSON API document that has already been parsed back into JSON format).
   *
   * @param   document
   *          The JSON API document.
   * @param   converter
   *          The resource converter to use for the operation.
   *
   * @return  A representation of the contents of the document, as JSON.
   *
   * @throws  DocumentSerializationException
   *          If an issue with the document or its contents prevents creating a JSON representation.
   */
  @SuppressWarnings("unchecked")
  public static String toJsonString(final JSONAPIDocument<?> document,
                                    final ResourceConverter converter)
  throws DocumentSerializationException {
    final byte[] jsonStringBytes;

    if (document.get() instanceof Iterable) {
      jsonStringBytes =
        converter.writeDocumentCollection((JSONAPIDocument<? extends Iterable<?>>)document);
    } else {
      jsonStringBytes = converter.writeDocument(document);
    }

    return new String(jsonStringBytes);
  }

  /**
   * Marshalls the provided JSON string into an instance of the specified model type.
   *
   * @param   jsonString
   *          The JSON to parse.
   * @param   modelType
   *          The type of model being instantiated from its JSON representation.
   *
   * @param   <T>
   *          The type of model.
   *
   * @return  The model parsed from the JSON.
   */
  public static <T extends Model> T fromJsonString(final String jsonString,
                                                   final Class<T> modelType) {
    return JSONUtils.fromJsonString(jsonString, modelType, modelType);
  }

  /**
   * Marshalls the provided JSON string into an instance of the specified model type, which may
   * include references to the specified types of models.
   *
   * @param   jsonString
   *          The JSON to parse.
   * @param   modelType
   *          The type of model being instantiated from its JSON representation.
   * @param   modelTypes
   *          All of the types of models that may have references in the JSON.
   *
   * @param   <T>
   *          The type of model.
   *
   * @return  The model parsed from the JSON.
   */
  @SafeVarargs
  public static <T extends Model> T fromJsonString(final String jsonString,
                                                   final Class<T> modelType,
                                                   final Class<? extends Model>... modelTypes) {
    final ResourceConverter converter = JSONUtils.createResourceConverterFor(modelTypes);

    return JSONUtils.fromJsonString(jsonString, modelType, converter);
  }

  /**
   * Marshalls the provided JSON string into an instance of the specified model type, using the
   * provided resource converter for the operation.
   *
   * @param   jsonString
   *          The JSON to parse.
   * @param   modelType
   *          The type of model being instantiated from its JSON representation.
   * @param   converter
   *          The resource converter to use for the operation.
   *
   * @param   <T>
   *          The type of model.
   *
   * @return  The model parsed from the JSON.
   */
  public static <T extends Model> T fromJsonString(final String jsonString,
                                                   final Class<T> modelType,
                                                   final ResourceConverter converter) {
    final JSONAPIDocument<T> document = converter.readDocument(jsonString.getBytes(), modelType);

    return document.get();
  }

}
