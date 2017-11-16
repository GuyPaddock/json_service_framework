package com.rosieapp.services.loyalty;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.rosieapp.services.loyalty.identifiers.JsonObjectIdHandler;
import com.rosieapp.services.loyalty.identifiers.ObjectIdentifier;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AbstractModel
implements Model {
  @Id(value = JsonObjectIdHandler.class)
  private ObjectIdentifier id;

  @Override
  public ObjectIdentifier getId() {
    return id;
  }

  public boolean isNew() {
    return (this.getId().isObjectNew());
  }

  protected void setId(final ObjectIdentifier id) {
    if (this.id != null) {
      throw new IllegalStateException("id is already set");
    }

    this.id = id;
  }
}
