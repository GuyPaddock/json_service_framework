/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.exception;

import java.text.MessageFormat;

public class RequestFailedException extends Exception {

  private static final String EXCEPTION_MESSAGE = "Request made to {0} at `{1}` has failed.";

  /**
   * Constructor for {@code RequestFailed}.
   *
   * <p>Initializes the exception to indicate that the request to the specified
   * service at the specified URL has failed.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is being contacted (e.g. "https://loyalty.rosieapp.com").
   */
  public RequestFailedException(final String serviceName, final String serviceUrl) {
    this(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl));
  }

  /**
   * Constructor for {@code RequestFailed}.
   *
   * <p>Initializes the exception to indicate that the request to the specified
   * service at the specified URL has failed.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is being contacted (e.g. "https://loyalty.rosieapp.com").
   * @param cause
   *        The underlying exception that was wrapped by this exception.
   */
  public RequestFailedException(final String serviceName, final String serviceUrl,
      final Throwable cause) {
    this(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl), cause);
  }

  /**
   * Initializes the exception with the provided detail message.
   *
   * @param message the detail message for this exception
   */
  public RequestFailedException(final String message) {
    super(message);
  }

  /**
   * Initializes the exception with the provided detail message and specified underlying cause.
   *
   * @param message the detail message for this exception
   *
   * @param cause
   *        The underlying exception that was wrapped by this exception.
   */
  protected RequestFailedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Generates an appropriate, human-friendly message for the exception.
   *
   * @param   serviceName
   *          The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param   serviceUrl
   *          The URL of the service that is unavailable (e.g. "https://loyalty.rosieapp.com").

   * @return  The generated exception message.
   */
  protected static String generateMessage(final String message,
                                          final String serviceName,
                                          final String serviceUrl) {

    return MessageFormat.format(message, serviceName, serviceUrl);
  }

}
