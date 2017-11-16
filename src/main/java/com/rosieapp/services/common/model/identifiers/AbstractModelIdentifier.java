package com.rosieapp.services.common.model.identifiers;

/**
 * Optional, abstract parent class provided for use by model identifiers in the system.
 * <p>
 * This implementation ensures that sub-classes provide an implementation of {@link #toString()}.
 */
public abstract class AbstractModelIdentifier
implements ModelIdentifier {
  @Override
  public abstract String toString();
}
