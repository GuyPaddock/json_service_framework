package com.rosieapp.services.common.model.identifiers;

/**
 * An identifier that is used for newly-created models that have not yet been persisted.
 */
public final class NewModelIdentifier
extends AbstractModelIdentifier {
  /**
   * A singleton instance of this identifier.
   */
  private static final NewModelIdentifier INSTANCE = new NewModelIdentifier();

  /**
   * Gets the singleton, flyweight instance of this identifier.
   * <p>
   * For efficiency, this is preferred to using the constructor since every instance has no
   * instance state and behaves exactly the same way.
   *
   * @return The identifier instance.
   */
  public static NewModelIdentifier getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor for {@link NewModelIdentifier}.
   *
   * @deprecated For efficiency, rather than constructing a new identifier, it is preferable to use
   *             {@link #getInstance()} instead.
   */
  public NewModelIdentifier() {
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code true}, always (since this identifier is only used for new objects).
   */
  @Override
  public boolean isObjectNew() {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code null}, to signify that this identifies a new, un-persisted object.
   */
  @Override
  public String toString() {
    return null;
  }
}
