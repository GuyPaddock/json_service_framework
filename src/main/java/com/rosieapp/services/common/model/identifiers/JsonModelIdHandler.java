package com.rosieapp.services.common.model.identifiers;

import com.github.jasminb.jsonapi.ResourceIdHandler;
import java.util.Optional;

/**
 * A {@link ResourceIdHandler} that is used to marshall model identifiers into and out of JSON
 * documents.
 */
public class JsonModelIdHandler
implements ResourceIdHandler {
  @Override
  public String asString(Object identifier) {
    return Optional.ofNullable(identifier).map(Object::toString).orElse(null);
  }

  @Override
  public Object fromString(String identifier) {
    return ModelIdentifierFactory.getInstance().createIdFrom(identifier);
  }
}