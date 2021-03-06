/*
 * Copyright (c) 2018 Rosie Applications Inc.
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

package com.rosieapp.services.common.client;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.google.common.collect.Iterables;
import com.rosieapp.services.common.annotation.JsonApiV1Noncompliant;
import com.rosieapp.services.common.interception.NoncompliantJsonContentTypeInterceptor;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.util.JsonUtils;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for a builder that provides a convenient way to
 * assemble a Retrofit client for a generic service interface.
 * <p>Concrete builders must implement {@link #getModelTypes()} to supply
 * a list of Model classes associated with the client being built and the concrete
 * builder's constructor must call the {@link ServiceClientBuilder} constructor and
 * provide the interface for the service. </p>
 */
public abstract class ServiceClientBuilder<T> {
  private final Class<? extends T> serviceInterface;

  private   String             baseUrl;
  private   Converter.Factory  converterFactory;

  /**
   * Constructor for {@code ServiceClientBuilder}.
   * Must be called by constructors of builder classes extending this base class to provide the
   * service interface
   *
   * @param serviceInterface The service interface for the client being built.
   */
  public ServiceClientBuilder(final Class<? extends T> serviceInterface) {
    this.serviceInterface = serviceInterface;
  }

  /**
   * Indicates that the client is being constructed only for performing a
   * health status check, rather than normal calls.
   *
   * <p>If the client is constructed for health status checks, it cannot be used to perform other
   * types of requests, and vice-versa. This is a consequence of the fact that the status check
   * endpoint is not JSON API v1 compliant and therefore requires a special resource converter.
   * This method should be removed if status check responses are made compliant.
   *
   * @return This object, for chaining builder calls.
   */
  @JsonApiV1Noncompliant(reason = JsonApiV1Noncompliant.REASON_BAD_RESPONSE_FORMAT)
  public ServiceClientBuilder<T> forHealthCheckRequests() {
    this.converterFactory = ScalarsConverterFactory.create();

    return this;
  }

  /**
   * Indicates that the client is being constructed for normal calls rather
   * than health status checks.
   *
   * <p>If the client is constructed for normal calls, it cannot be used to perform health status
   * checks, and vice-versa. This is a consequence of the fact that the status check endpoint is
   * not JSON API v1 compliant and therefore requires a special resource converter. This method
   * should be removed if status check responses are made compliant.
   *
   * @return This object, for chaining builder calls.
   */
  @JsonApiV1Noncompliant(reason = JsonApiV1Noncompliant.REASON_BAD_RESPONSE_FORMAT)
  public ServiceClientBuilder<T> forNormalRequests() {
    final ResourceConverter resourceConverter = this.createResourceConverter();

    this.converterFactory = new JSONAPIConverterFactory(resourceConverter);

    return this;
  }

  /**
   * Must be implemented by concrete builder classes to provide the JSON converter
   * with the data models used in the service's API.
   *
   * @return a list of data model types.
   */
  protected abstract List<Class<? extends Model>> getModelTypes();

  /**
   * Sets the protocol, host, and optionally, the port, where the service is hosted (e.g.
   * https://somewhere.rosieapp.com or http://local.rosieapp.com:8080).
   *
   * @param baseUrl The base URL to use for the next client instance this builder creates.
   * @return This object, for chaining builder calls.
   */
  public ServiceClientBuilder<T> withBaseUrl(final String baseUrl) {
    this.baseUrl = baseUrl;

    return this;
  }

  /**
   * Creates a new service client, using the current values set on this builder.
   *
   * @return The new client instance.
   *
   * @throws NullPointerException
   *         If the {@code baseUrl} has not been set on the builder before
   *         calling this method.
   */
  public T build() throws NullPointerException {
    final OkHttpClient client;
    final Retrofit retrofit;

    this.validateArguments();

    client =
      new OkHttpClient.Builder()
        .addInterceptor(new NoncompliantJsonContentTypeInterceptor())
        .build();

    retrofit =
      new Retrofit.Builder()
        .baseUrl(this.baseUrl)
        .client(client)
        .addConverterFactory(this.converterFactory)
        .build();

    return retrofit.create(this.serviceInterface);
  }

  /**
   * Ensures that all the values necessary to build a client have been set.
   *
   * @throws NullPointerException If any required values are missing.
   */
  private void validateArguments()
  throws NullPointerException {
    Objects.requireNonNull(this.baseUrl, "baseUrl cannot be null");
  }


  /**
   * Creates the JSON API Resource Converter, which handles serialization of objects to, and
   * deserialization of objects from, JSON API v1 standard format.
   *
   * @return  The resource converter to use for serialization.
   */
  @SuppressWarnings("unchecked")
  private ResourceConverter createResourceConverter() {
    final ResourceConverter             converter;
    final List<Class<? extends Model>>  modelTypes = this.getModelTypes();

    converter =
      JsonUtils.createResourceConverterThatIncludesRelationshipsFor(
        Iterables.toArray(modelTypes, Class.class));

    return converter;
  }
}
