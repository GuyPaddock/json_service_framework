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

package com.rosieapp.services.common.exception;

/**
 * Exception thrown when the credentials supplied for authenticating with a service are incorrect.
 */
public class InvalidServiceCredentialsException
extends RequestFailedException {
  private static final String EXCEPTION_MESSAGE =
    "The credentials provided for connecting to the {0} at `{1}` are incorrect.";

  /**
   * Constructor for {@code InvalidServiceCredentialsException}.
   *
   * <p>Initializes the exception to indicate that credentials are incorrect for the specified
   * service at the specified URL.
   *
   * @param serviceName
   *        The human-friendly name of the service (e.g. "Loyalty Service", "ECOM', etc).
   * @param serviceUrl
   *        The URL of the service that is being contacted (e.g. "https://loyalty.rosieapp.com").
   */
  public InvalidServiceCredentialsException(final String serviceName, final String serviceUrl) {
    super(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl));
  }

  /**
   * Constructor for {@code InvalidServiceCredentialsException}.
   *
   * <p>Initializes the exception to indicate that credentials are incorrect for the specified
   * service at the specified URL, and that the issue with credentials is caused by the specified
   * underlying cause.
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
    super(generateMessage(EXCEPTION_MESSAGE, serviceName, serviceUrl), cause);
  }
}
