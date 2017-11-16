package com.rosieapp.services.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Id;
import com.rosieapp.services.loyalty.identifiers.JsonObjectIdHandler;
import com.rosieapp.services.loyalty.identifiers.ObjectIdentifier;

public interface Model {
  public ObjectIdentifier getId();

  @JsonIgnore
  public boolean isNew();
}
