package com.rosieapp.services.loyalty.identifiers;

public abstract class ExistingObjectIdentifier
extends AbstractObjectIdentifier {
  @Override
  public final boolean isObjectNew() {
    return false;
  }
}
