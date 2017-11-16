package com.rosieapp.services.loyalty;

import com.rosieapp.services.loyalty.identifiers.NewObjectIdentifier;
import com.rosieapp.services.loyalty.identifiers.ObjectIdentifier;
import com.rosieapp.services.loyalty.identifiers.ObjectIdentifierFactory;
import java.util.Optional;

public abstract class AbstractModelBuilder<T extends Model, B extends ModelBuilder<T>>
implements ModelBuilder<T> {
  private ObjectIdentifier id;

  public B withId(String id) {
    return this.withId(ObjectIdentifierFactory.getInstance().createIdFrom(id));
  }

  public B withId(ObjectIdentifier id) {
    this.id = id;

    return (B)this;
  }

  protected ObjectIdentifier buildId() {
    return Optional.ofNullable(this.id).orElse(new NewObjectIdentifier());
  }
}
