package com.rosieapp.common.collections;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.greghaskins.spectrum.Spectrum;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class MapsTests {
  {
    describe("toString(Map)", () -> {
      context("when the map is empty", () -> {
        final Supplier<Map<String, String>> map = let(MapsTests::createEmptyMap);

        it("returns an empty string", () -> {
          assertThat(Maps.toString(map.get())).isEmpty();
        });
      });

      context("when the map has one entry", () -> {
        final Supplier<Map<String, String>> map = let(MapsTests::createMapWithOneElement);

        it("returns a string containing the key and value of the entry", () -> {
          assertThat(Maps.toString(map.get())).isEqualTo("\"key1\": \"value1\"");
        });
      });

      context("when the map has multiple entries", () -> {
        context("when the map maintains a sort order", () -> {
          final Supplier<Map<String, String>> map = let(MapsTests::createTreeMap);

          it("returns a comma-separated string of entries, in the order they appear in the " +
             "map", () -> {
            assertThat(Maps.toString(map.get()))
              .isEqualTo("\"key1\": \"value2\", \"key2\": \"value1\"");
          });
        });

        context("when the map orders entries by hash code", () -> {
          final Supplier<Map<String, String>> map = let(MapsTests::createHashMap);

          it("returns a comma-separated string of entries, in whatever order the map returns " +
             "them", () -> {
            assertThat(Maps.toString(map.get()))
              .isEqualTo("\"key1\": \"value3\", \"key2\": \"value2\", \"id\": \"value1\"");
          });
        });
      });
    });

    describe("toStringSorted(Map)", () -> {
      context("when the map is empty", () -> {
        final Supplier<Map<String, String>> map = let(MapsTests::createEmptyMap);

        it("returns an empty string", () -> {
          assertThat(Maps.toStringSorted(map.get())).isEmpty();
        });
      });

      context("when the map has one entry", () -> {
        final Supplier<Map<String, String>> map = let(MapsTests::createMapWithOneElement);

        it("returns a string containing the key and value of the entry", () -> {
          assertThat(Maps.toStringSorted(map.get())).isEqualTo("\"key1\": \"value1\"");
        });
      });

      context("when the map has multiple entries", () -> {
        context("when the map maintains an insertion order", () -> {
          final Supplier<Map<String, String>> map = let(MapsTests::createLinkedHashMap);

          it("returns a comma-separated string of entries, in alphabetic order by key", () -> {
            assertThat(Maps.toStringSorted(map.get()))
              .isEqualTo("\"key1\": \"value3\", \"key2\": \"value1\", \"key3\": \"value2\"");
          });
        });

        context("when the map orders entries by hash code", () -> {
          final Supplier<Map<String, String>> map = let(MapsTests::createHashMap);

          it("returns a comma-separated string of entries, in whatever order the map returns " +
             "them", () -> {
            assertThat(Maps.toStringSorted(map.get()))
              .isEqualTo("\"id\": \"value1\", \"key1\": \"value3\", \"key2\": \"value2\"");
          });
        });
      });
    });

    describe("toString(Stream)", () -> {
      context("when the stream supplies no entries", () -> {
        final Supplier<Stream<Entry<String, String>>> stream = let(Stream::of);

        it("returns an empty string", () -> {
          assertThat(Maps.toString(stream.get())).isEmpty();
        });
      });

      context("when the stream supplies one entry", () -> {
        final Supplier<Stream<Entry<String, String>>> stream =
          let(() -> Stream.of(new SimpleEntry<>("key", "value")));

        it("returns a string containing the key and value of the entry", () -> {
          assertThat(Maps.toString(stream.get())).isEqualTo("\"key\": \"value\"");
        });
      });

      context("when the stream supplies multiple entries", () -> {
        final Supplier<Stream<Entry<String, String>>> stream =
          let(() ->
            Stream.of(
              new SimpleEntry<>("key2", "value2"),
              new SimpleEntry<>("key1", "value1")));

        it("returns a comma-separated string of entries, in whatever order the stream returns " +
           "them", () -> {
          assertThat(Maps.toString(stream.get()))
            .isEqualTo("\"key2\": \"value2\", \"key1\": \"value1\"");
        });
      });
    });
  }

  private static Map<String, String> createEmptyMap() {
    return Collections.emptyMap();
  }

  private static Map<String, String> createMapWithOneElement() {
    final Map<String, String> result = new HashMap<>();

    result.put("key1", "value1");

    return result;
  }

  private static Map<String, String> createHashMap() {
    final Map<String, String> result = new HashMap<>();

    result.put("id", "value1");
    result.put("key2", "value2");
    result.put("key1", "value3");

    return result;
  }

  private static Map<String, String> createLinkedHashMap() {
    final Map<String, String> result = new LinkedHashMap<>();

    result.put("key2", "value1");
    result.put("key3", "value2");
    result.put("key1", "value3");

    return result;
  }

  private static Map<String, String> createTreeMap() {
    final Map<String, String> result = new TreeMap<>();

    result.put("key2", "value1");
    result.put("key1", "value2");

    return result;
  }
}
