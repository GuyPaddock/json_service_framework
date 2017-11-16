package com.rosieapp.services.common.model.field;

public interface FieldHandler {
  <T> T requireField(T fieldValue, String fieldName);
  <T> T getFieldOrDefault(T fieldValue, T defaultValue);
}
