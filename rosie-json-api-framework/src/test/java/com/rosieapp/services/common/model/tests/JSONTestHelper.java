package com.rosieapp.services.common.model.tests;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.EnumSet;
import java.util.Set;

public final class JSONTestHelper {
  /**
   * Private constructor for singleton utility class.
   */
  private JSONTestHelper() {
  }

  /**
   * Sets up a test for AssertJ JSON assertions to leverage Jackson serialization and
   * de-serialization.
   */
  public static void configureTestForJackson() {
    Configuration.setDefaults(new Configuration.Defaults() {
      private final JsonProvider jsonProvider    = new JacksonJsonProvider();
      private final MappingProvider mappingProvider = new JacksonMappingProvider();

      @Override
      public JsonProvider jsonProvider() {
        return jsonProvider;
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }
    });
  }

}
