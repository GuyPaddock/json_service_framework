package com.rosieapp.services.common.interception;

import com.rosieapp.services.common.annotation.JSONAPIV1Noncompliant;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * An interceptor that modifies the content type on an outgoing JSON API request.
 * <p>
 * The content type is changed to a non-standard value of {@code application/json} instead of the
 * JSON-API-standard-compliant value of {@code application/vnd.api+json}. This works around
 * limitations in Rosie's legacy Rails services that do not handle the
 * {@code application/vnd.api+json} content type properly.
 */
@JSONAPIV1Noncompliant(reason = "Content type for requests is required to be application/vnd.api+json")
public class NoncompliantJsonContentTypeInterceptor
implements Interceptor {
  @Override
  public Response intercept(Chain chain)
  throws IOException {
    final Request     originalRequest = chain.request(),
                      newRequest;
    final RequestBody originalBody    = originalRequest.body();

    // GET requests don't have a body.
    if (originalBody == null) {
      newRequest = originalRequest;
    }
    else {
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
