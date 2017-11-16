package com.rosieapp.services.common.model.builder;

import com.rosieapp.common.collections.Maps;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.field.FieldHandler;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MapBasedModelBuilder<T extends Model, B extends ModelBuilder<T>>
extends AbstractModelBuilder<T, B> {
  private final Map<String, Object> fieldValues;

  @Override
  public String toString() {
    final String className = this.getClass().getCanonicalName(),
                 valueStr  = this.getFieldValuesAsString();

    return String.format("%s{%s}", className, valueStr);
  }

  protected MapBasedModelBuilder(final FieldHandler fieldHandler) {
    super(fieldHandler);

    this.fieldValues = new HashMap<>();
  }

  protected void putFieldValue(String fieldName, Object value) {
    this.getFieldValues().put(fieldName, value);
  }

  @SuppressWarnings("unchecked")
  protected <P> P getFieldValue(String fieldName) {
    return (P)this.getFieldValues().get(fieldName);
  }

  protected <P> P requireField(final String fieldName) {
    return this.getFieldHandler().requireField(this.getFieldValue(fieldName), fieldName);
  }

  protected <P> P getFieldOrDefault(final String fieldName, final P defaultValue) {
    return this.getFieldHandler().getFieldOrDefault(this.getFieldValue(fieldName), defaultValue);
  }

  private Map<String, Object> getFieldValues() {
    return this.fieldValues;
  }

  private String getFieldValuesAsString() {
    final String string;

    string =
      Maps.toString(
        Stream.concat(
          Stream.of(new SimpleEntry<>("id", this.buildId())),
          this.getFieldValues().entrySet().stream()
        ));

    return string;
  }
}
