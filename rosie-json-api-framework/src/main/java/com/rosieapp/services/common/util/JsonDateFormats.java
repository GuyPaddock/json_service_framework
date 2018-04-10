/*
 * Copyright (c) 2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.util;

/**
 * Several common date formats that can be used with Jackson annotations.
 *
 * <p>For example:
 *
 * <pre>
 * {@code @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonDateFormats.ISO8601_DATE)}
 * </pre>
 */
public final class JsonDateFormats {
  public static final String ISO8601_DATE = "yyyy-MM-dd";
  public static final String ISO8601_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  private JsonDateFormats() {}
}
