/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.identification;

/**
 * Optional, abstract parent class provided for use by identifiers of models that have been
 * persisted.
 *
 * <p>This implementation provides built-in handling for the {@link #isObjectNew()} method.
 */
public abstract class PersistedModelIdentifier
extends AbstractModelIdentifier {
  @Override
  public final boolean isObjectNew() {
    return false;
  }
}
