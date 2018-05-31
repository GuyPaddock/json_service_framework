/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.exception;


/**
 * Exception thrown when a web service is down or unable to respond successfully to requests.
 */
public class ServiceUnavailableException
extends RequestFailedException {

  private static final String
      EXCEPTION_MESSAGE = "The {0} at `{1}` is unhealthy and/or unavailable.";

  /**
   * Constructor for {@code ServiceUnavailableException}.
   *
   * <p>Initializes the exception to indicate that the specified service is unavailable at the
   * specified URL.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is unavailable (e.g. "https://loyalty.rosieapp.com").
   */
  public ServiceUnavailableException(final String serviceName, final String serviceUrl) {
    super(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl));
  }

  /**
   * Constructor for {@code ServiceUnavailableException}.
   *
   * <p>Initializes the exception to indicate that the specified service is unavailable at the
   * specified URL, and that the unavailability is caused by the specified underlying cause.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is unavailable (e.g. "https://loyalty.rosieapp.com").
   * @param cause
   *        The underlying exception that was wrapped by this exception.
   */
  public ServiceUnavailableException(final String serviceName, final String serviceUrl,
                                     final Throwable cause) {
    super(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl), cause);
  }

}
