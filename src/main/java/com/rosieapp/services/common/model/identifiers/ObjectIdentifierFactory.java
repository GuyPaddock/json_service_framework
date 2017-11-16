package com.rosieapp.services.common.model.identifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class ObjectIdentifierFactory {
  private static final ObjectIdentifierFactory INSTANCE = new ObjectIdentifierFactory();

  private static final List<Function<String, Optional<ObjectIdentifier>>> STRATEGIES =
    Arrays.asList(
      LongIdentifier::createFrom,
      UUIDIdentifier::createFrom
    );

  public static ObjectIdentifierFactory getInstance() {
    return INSTANCE;
  }

  public ObjectIdentifier createIdFrom(final long identifier) {
    return new LongIdentifier(identifier);
  }

  public ObjectIdentifier createIdFrom(final UUID identifier) {
    return new UUIDIdentifier(identifier);
  }

  public ObjectIdentifier createIdFrom(final String identifier) {
    return STRATEGIES.stream()
      .map((strategy) -> strategy.apply(identifier))
      .filter(Optional::isPresent)
      .findFirst()
      .orElseThrow(
        () -> new IllegalArgumentException("Unrecognized identifier format: " + identifier))
      .get();
  }
}
