package com.rosieapp.services.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rosieapp.services.common.model.identifiers.ObjectIdentifier;

public interface Model {
  ObjectIdentifier getId();

  @JsonIgnore
  boolean isNew();
}
