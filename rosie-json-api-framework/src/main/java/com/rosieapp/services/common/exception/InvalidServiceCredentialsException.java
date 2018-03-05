package com.rosieapp.services.common.exception;

import java.text.MessageFormat;

/**
 * Exception thrown when the credentials supplied for authenticating with a service are incorrect.
 */
public class InvalidServiceCredentialsException
extends RuntimeException {
  /**
   * Constructor for {@code ServiceUnavailableException}.
   * <p>
   * Initializes the exception to indicate that credentials are incorrect for the specified service
   * at the specified URL.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is being contacted (e.g. "https://loyalty.rosieapp.com").
   */
  public InvalidServiceCredentialsException(final String serviceName, final String serviceUrl) {
    super(generateMessage(serviceName, serviceUrl));
  }

  /**
   * Constructor for {@code ServiceUnavailableException}.
   * <p>
   * Initializes the exception to indicate that credentials are incorrect for the specified service
   * at the specified URL, and that the issue with credentials is caused by the specified underlying
   * cause.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is being contacted (e.g. "https://loyalty.rosieapp.com").
   * @param cause
   *        The underlying exception that was wrapped by this exception.
   */
  public InvalidServiceCredentialsException(final String serviceName, final String serviceUrl,
                                            final Throwable cause) {
    super(generateMessage(serviceName, serviceUrl), cause);
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
  private static String generateMessage(final String serviceName, final String serviceUrl) {
    return MessageFormat.format(
      "The credentials provided for connecting to the {0} at `{1}` are incorrect.",
      serviceName,
      serviceUrl);
  }
}
