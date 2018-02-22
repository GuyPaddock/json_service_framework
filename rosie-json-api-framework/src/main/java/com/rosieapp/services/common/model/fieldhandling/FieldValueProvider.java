/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model.fieldhandling;

/**
 * Common interface for objects that provide the values that builders use to populate the fields of
 * the Rosie JSON API service models they are constructing.
 *
 * <p>Field value providers typically fall into two categories:
 * <ul>
 *   <li>Strict value providers raise errors when a required field lacks a value.
 *   <li>Lax value providers substitute a value when a required field lacks a value.
 * </ul>
 *
 * <p>However, this interface imposes no requirements on how a Field Value Provider actually
 * interprets requests for field values, nor on how it retrieves or validates the field values it
 * returns.
 */
public interface FieldValueProvider {
  /**
   * Optionally validates and then returns the value to use when populating the specified required
   * field.
   * <p>
   * Depending upon implementation, if the field value is {@code null}, the field provider may
   * choose to communicate this by raising an {@link IllegalStateException}, or it may simply supply
   * {@code null} (or a different value of its own choosing) in place of the missing value.
   *
   * @param   fieldValue
   *          The current value for the field (typically supplied by the builder).
   * @param   fieldName
   *          The name of the field. This may be used by the implementation to construct an
   *          exception message if the field has no value.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the current implementation, this will typically be a non-null value to
   *          use for the field, but may be {@code null} if the implementation is lax on validating
   *          that all required fields are populated.
   *
   * @throws  RequiredFieldMissingException
   *          If the required field value is {@code null} or invalid, and the current implementation
   *          considers this to be an error.
   */
  <F> F getRequiredField(F fieldValue, String fieldName) throws RequiredFieldMissingException;

  /**
   * Returns the value to use when populating the specified optional field.
   * <p>
   * Depending upon implementation, if the field value is {@code null}, then in place of the missing
   * value, the field provider may choose to return the {@code defaultValue} that has been provided,
   * or any other value of the provider's choosing.
   *
   * @param   fieldValue
   *          The current value this builder has for the field.
   * @param   defaultValue
   *          The default value that the builder would prefer to receive if the {@code fieldValue}
   *          is {@code null}.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field value provider, this will typically be either the value of
   *          the requested field, or the default value if the field did not have a value.
   */
  <F> F getOptionalField(F fieldValue, F defaultValue);
}
