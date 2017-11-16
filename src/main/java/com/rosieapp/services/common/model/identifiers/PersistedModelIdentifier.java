package com.rosieapp.services.common.model.identifiers;

/**
 * Optional, abstract parent class provided for use by identifiers of models that have been
 * persisted.
 *
 * This implementation provides built-in handling for the {@link #isObjectNew()} method.
 */
public abstract class PersistedModelIdentifier
extends AbstractModelIdentifier {
  @Override
  public final boolean isObjectNew() {
    return false;
  }
}
