package com.rosieapp.services.loyalty.identifiers;

import java.util.Optional;
import java.util.UUID;

public final class UUIDIdentifier extends ExistingObjectIdentifier {
  private UUID value;

  public static Optional<ObjectIdentifier> createFrom(final String value) {
    Optional<ObjectIdentifier> result;

    try {
      result = Optional.of(new UUIDIdentifier(value));
    }
    catch (IllegalArgumentException ex) {
      result = Optional.empty();
    }

    return result;
  }

  public UUIDIdentifier() {
    this(UUID.randomUUID());
  }

  public UUIDIdentifier(final String value) {
    this(UUID.fromString(value));
  }

  public UUIDIdentifier(final UUID value) {
    this.setValue(value);
  }

  protected UUID getValue() {
    return this.value;
  }

  protected void setValue(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }

    this.value = value;
  }

  @Override
  public String toString() {
    return this.getValue().toString();
  }
}
