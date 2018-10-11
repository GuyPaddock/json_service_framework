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

package com.rosieapp.services.common.util;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A class for common utility methods needed when dealing with JSON requests.
 */
public final class Requests {
  /**
   * Private constructor to ensure this class is static.
   */
  private Requests() {
  }

  /**
   * Converts an unsuccessful Retrofit response into a string that is suitable for an error message.
   *
   * @param   contextMessage
   *          A message explaining what failed (e.g. "failed to retrieve user").
   * @param   response
   *          The response from the server that indicates failure.
   *
   * @return  A string describing the failure.
   *
   * @throws  IllegalArgumentException
   *          If the provided response was successful (i.e. it was not a failure response).
   */
  public static String failureResponseToString(final String contextMessage,
                                               final Response<?> response) {
    final StringBuilder errorBuilder;
    final ResponseBody  errorBody;
    final String        responseMessage;

    // Sanity check
    if (response.isSuccessful()) {
      throw new IllegalArgumentException(
        "Response did not fail -- it was successful. This method must not be called for "
        + "successful responses.");
    }

    errorBuilder    = new StringBuilder();
    errorBody       = response.errorBody();
    responseMessage = response.message();

    errorBuilder.append(contextMessage).append(": ").append(response.code());

    if ((responseMessage != null) && !responseMessage.isEmpty()) {
      errorBuilder.append(" - ").append(responseMessage);
    }

    if (errorBody != null) {
      final String responseBody = responseBodyToString(errorBody);

      if (!responseBody.isEmpty()) {
        errorBuilder.append(' ').append(responseBody);
      }
    }

    return errorBuilder.toString();
  }

  /**
   * Converts a response body into a format suitable for inclusion in error messages and log
   * messages.
   *
   * @param   responseBody
   *          The response body to convert to a string.
   *
   * @return  A string that represents the contents of the response body.
   */
  private static String responseBodyToString(final ResponseBody responseBody) {
    String result = "";

    try {
      final String bodyString = responseBody.string();

      if (bodyString != null) {
        result = bodyString.trim();
      }
    } catch (IOException ex) {
      // Suppress error -- it is not useful to bubble it up because the only reason we're
      // trying to get the response is for diagnostic purposes in the first place.
      result = "ERROR: Failed to convert response body to string: " + ex.toString();
    }

    return result;
  }
}
