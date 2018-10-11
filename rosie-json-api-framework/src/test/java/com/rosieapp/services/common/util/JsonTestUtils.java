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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.EnumSet;
import java.util.Set;

/**
 * A class that contains commonly-needed utility functions for JSON-related tests.
 */
public final class JsonTestUtils {
  /**
   * Private constructor for singleton utility class.
   */
  private JsonTestUtils() {
  }

  /**
   * Sets up a test for AssertJ JSON assertions to leverage Jackson serialization and
   * de-serialization.
   */
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static void configureTestForJackson() {
    Configuration.setDefaults(new Configuration.Defaults() {
      private final JsonProvider jsonProvider = new JacksonJsonProvider();
      private final MappingProvider mappingProvider = new JacksonMappingProvider();

      @Override
      public JsonProvider jsonProvider() {
        return this.jsonProvider;
      }

      @Override
      public MappingProvider mappingProvider() {
        return this.mappingProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }
    });
  }
}
