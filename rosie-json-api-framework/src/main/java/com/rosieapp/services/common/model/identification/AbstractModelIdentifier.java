/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.identification;

/**
 * Optional, abstract parent class provided for use by model identifiers in the system.
 *
 * <p>This implementation ensures that sub-classes provide an implementation of {@link #toString()}.
 */
public abstract class AbstractModelIdentifier
implements ModelIdentifier {
  @Override
  public abstract String toString();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();
}
