/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.annotation;

/**
 * This annotation marks all objects that currently exist solely to provide workarounds for parts of
 * Rosie's legacy JSON-based service API that are not compliant with the JSON API v1 specification.
 *
 * <p>The long-term goal is to bring all legacy services into compliance with the specification so
 * that all classes tagged with this annotation can be removed in their entirety.
 */
public @interface JsonApiV1Noncompliant {
  /**
   * Indicates that a given endpoint is not compliant with the JSON API specification either because
   * it does not accept requests that use the required content type; or because it does not send a
   * response with the required content type.
   */
  String REASON_BAD_CONTENT_TYPE =
    "Content type for requests is required to be application/vnd.api+json";

  /**
   * Indicates that a given operation or endpoint returns a response that does not follow the
   * conventions of JSON API v1.
   *
   * <p>For example, the operation might return a response that is plain text, or a JSON payload
   * that is not structured properly.
   */
  String REASON_BAD_RESPONSE_FORMAT = "Response is not formatted according to JSON API v1.";

  /**
   * Indicates that a given field within the schema of a resource is only used for some requests
   * but is not actually a part of the schema for that resource.
   *
   * <p>This often indicates fields that are being used like RPC-style parameters, rather than
   * REST-style resource fields. Such fields can be counter-intuitive to the client, since a value
   * written to a resource in one request will seemingly disappear during subsequent interactions
   * with the server.
   *
   * <p>This breaks the schema consistency implied by the semantics of the JSON API
   * specification. Specifically:
   * <ol>
   *   <li>Each distinct "type" identifies a given namespace of fields.
   *       See: http://jsonapi.org/format/#document-resource-object-identification</li>
   *   <li>The response to PATCH requests is expected to include the updated fields).
   *       See: http://jsonapi.org/format/#crud-updating-responses-200</li>
   * </ol>
   */
  String REASON_BAD_RESOURCE_SCHEMA =
    "One or more fields in the resource only appear in client requests but not in responses.";

  /**
   * Provides the reason for non-compliance.
   *
   * @return  The reason why the object tagged with this annotation is not compliant with the JSON
   *          API v1 specification.
   */
  String reason();
}
