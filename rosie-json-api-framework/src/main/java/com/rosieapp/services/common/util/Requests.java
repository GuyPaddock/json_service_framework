/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.util;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A class for common utility methods needed when dealing with JSON requests.
 */
public class Requests {
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
    if (response.isSuccessful()) {
      throw new IllegalArgumentException(
        "Response did not fail -- it was successful. This method must not be called for "
        + "successful responses.");
    } else {
      final StringBuilder errorBuilder    = new StringBuilder();
      final ResponseBody  errorBody       = response.errorBody();
      final String        responseMessage = response.message();

      errorBuilder.append(String.format("%s: %d", contextMessage, response.code()));

      if ((responseMessage != null) && !responseMessage.isEmpty()) {
        errorBuilder.append(" - ");
        errorBuilder.append(responseMessage);
      }

      if ((errorBody != null)) {
        try {
          final String errorBodyString = errorBody.string();

          if ((errorBodyString != null) && !errorBodyString.isEmpty()) {
            errorBuilder.append(' ');
            errorBuilder.append(errorBodyString);
          }
        } catch (IOException ex) {
          // Suppress error -- it is not useful to handle it because the only reason we're trying to
          // get the response is for logging purposes in the first place.
        }
      }

      return errorBuilder.toString();
    }
  }
}
