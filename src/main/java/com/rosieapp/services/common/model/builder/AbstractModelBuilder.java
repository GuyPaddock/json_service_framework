package com.rosieapp.services.common.model.builder;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.field.FieldHandler;
import com.rosieapp.services.common.model.identifiers.NewObjectIdentifier;
import com.rosieapp.services.common.model.identifiers.ObjectIdentifier;
import com.rosieapp.services.common.model.identifiers.ObjectIdentifierFactory;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AbstractModelBuilder<T extends Model, B extends ModelBuilder<T>>
implements ModelBuilder<T> {
  private final FieldHandler fieldHandler;

  private ObjectIdentifier id;

  protected AbstractModelBuilder(final FieldHandler fieldHandler) {
    this.fieldHandler = fieldHandler;
  }

  public B withId(final String id) {
    return this.withId(ObjectIdentifierFactory.getInstance().createIdFrom(id));
  }

  public B withId(final ObjectIdentifier id) {
    this.id = id;

    return (B)this;
  }

  protected ObjectIdentifier buildId() {
    return Optional.ofNullable(this.id).orElse(new NewObjectIdentifier());
  }

  protected <T> T requireField(final T fieldValue, final String fieldName) {
    return this.getFieldHandler().requireField(fieldValue, fieldName);
  }

  protected <T> T getFieldOrDefault(final T fieldValue, final T defaultValue) {
    return this.getFieldHandler().getFieldOrDefault(fieldValue, defaultValue);
  }

  protected FieldHandler getFieldHandler() {
    return this.fieldHandler;
  }
}
