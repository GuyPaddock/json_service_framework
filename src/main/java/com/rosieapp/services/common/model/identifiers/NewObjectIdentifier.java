package com.rosieapp.services.common.model.identifiers;

public final class NewObjectIdentifier
extends AbstractObjectIdentifier {
  @Override
  public boolean isObjectNew() {
    return true;
  }

  @Override
  public String toString() {
    return null;
  }
}
