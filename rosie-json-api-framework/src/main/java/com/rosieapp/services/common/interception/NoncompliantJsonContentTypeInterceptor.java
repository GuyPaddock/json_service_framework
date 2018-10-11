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

package com.rosieapp.services.common.interception;

import com.rosieapp.services.common.annotation.JsonApiV1Noncompliant;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * An interceptor that modifies the content type on an outgoing JSON API request.
 *
 * <p>The content type is changed to a non-standard value of {@code application/json} instead of the
 * JSON-API-standard-compliant value of {@code application/vnd.api+json}. This works around
 * limitations in Rosie's legacy Rails services that do not handle the
 * {@code application/vnd.api+json} content type properly.
 */
@JsonApiV1Noncompliant(reason = JsonApiV1Noncompliant.REASON_BAD_CONTENT_TYPE)
public class NoncompliantJsonContentTypeInterceptor
implements Interceptor {
  @Override
  public Response intercept(final Chain chain)
  throws IOException {
    final Request     originalRequest = chain.request(),
                      newRequest;
    final RequestBody originalBody    = originalRequest.body();

    // GET requests don't have a body.
    if (originalBody == null) {
      newRequest = originalRequest;
    } else {
      newRequest = rebuildRequest(originalRequest);
    }

    return chain.proceed(newRequest);
  }

  /**
   * Rebuilds the body of the specified request, using the "application/json" content type for the
   * request.
   *
   * @param   request
   *          The request to rebuild.
   *
   * @return  The rebuilt request.
   *
   * @throws  IOException
   *          If the content of the request cannot be read or written.
   */
  private Request rebuildRequest(final Request request)
  throws IOException {
    final String      newContentType  = "application/json";
    final Request     newRequest;
    final RequestBody originalBody    = request.body(),
                      newBody;
    final Buffer      requestBuffer   = new Buffer();

    assert originalBody != null;

    originalBody.writeTo(requestBuffer);

    newBody =
      RequestBody.create(
        MediaType.parse(newContentType),
        requestBuffer.readByteArray());

    newRequest =
      request.newBuilder()
        .method(request.method(), newBody)
        .build();

    return newRequest;
  }
}
