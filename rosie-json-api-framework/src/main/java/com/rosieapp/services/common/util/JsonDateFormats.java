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
