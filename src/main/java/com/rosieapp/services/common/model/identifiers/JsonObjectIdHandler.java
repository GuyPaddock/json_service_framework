package com.rosieapp.services.common.model.identifiers;

import com.github.jasminb.jsonapi.ResourceIdHandler;
import java.util.Optional;

public class JsonObjectIdHandler
implements ResourceIdHandler {
  @Override
  public String asString(Object identifier) {
    return Optional.ofNullable(identifier).map(Object::toString).orElse(null);
  }

  @Override
  public Object fromString(String identifier) {
    return ObjectIdentifierFactory.getInstance().createIdFrom(identifier);
  }
}
