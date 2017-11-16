package com.rosieapp.services.common.model.identifiers;

public abstract class ExistingObjectIdentifier
extends AbstractObjectIdentifier {
  @Override
  public final boolean isObjectNew() {
    return false;
  }
}
