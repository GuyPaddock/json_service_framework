package com.rosieapp.services.common.model.identifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * A convenient factory for transforming values that represent model identifier values into their
 * corresponding {@link ModelIdentifier} instances.
 *
 * This factory is a singleton. Use {@link #getInstance()} to get a reference to the factory
 * instance.
 */
public class ModelIdentifierFactory {
  /**
   * The singleton instance of this factory.
   */
  private static final ModelIdentifierFactory INSTANCE = new ModelIdentifierFactory();

  /**
   * A reference to each of the parsing methods that are invoked, in the order that they appear,
   * when attempting to transform a string value into the most appropriate model identifier for the
   * type of identifier the string represents.
   */
  private static final List<Function<String, Optional<ModelIdentifier>>> STRATEGIES =
    Arrays.asList(
      LongIdentifier::createFrom,
      UUIDIdentifier::createFrom,
      StringIdentifier::createFrom
    );

  /**
   * Gets the singleton instance of this factory.
   *
   * @return  The instance of this factory.
   */
  public static ModelIdentifierFactory getInstance() {
    return INSTANCE;
  }

  /**
   * Attempts to parse the provided string representation of an identifier into the type of model
   * identifier that is most appropriate for the type of identifier the string represents.
   *
   * @param   identifier
   *          The string to attempt to convert into a model identifier.
   *
   * @return  A model identifier that was created by parsing the given identifier string.
   *
   * @throws  IllegalArgumentException
   *          If none of the known identifier formats matched the format of the provided identifier
   *          string.
   */
  public ModelIdentifier createIdFrom(final String identifier)
  throws IllegalArgumentException {
    return STRATEGIES.stream()
      .map((strategy) -> strategy.apply(identifier))
      .filter(Optional::isPresent)
      .findFirst()
      .orElseThrow(
        () -> new IllegalArgumentException("Unrecognized identifier format: " + identifier))
      .get();
  }

  /**
   * Converts the specified long integer identifier into a {@link ModelIdentifier} instance.
   *
   * @param   identifier
   *          The identifier to convert into a model identifier.
   *          This value must not be {@code null}.
   *
   * @return  A model identifier that wraps the provided long integer.
   *
   * @throws  IllegalArgumentException
   *          If {@code identifier} is {@code null}.
   */
  public ModelIdentifier createIdFrom(final long identifier) {
    return new LongIdentifier(identifier);
  }

  /**
   * Converts the specified UUID into a {@link ModelIdentifier} instance.
   *
   * @param   identifier
   *          The identifier to convert into a model identifier.
   *          This value must not be {@code null}.
   *
   * @return  A model identifier that wraps the provided UUID.
   *
   * @throws  IllegalArgumentException
   *          If {@code identifier} is {@code null}.
   */
  public ModelIdentifier createIdFrom(final UUID identifier)
  throws IllegalArgumentException {
    return new UUIDIdentifier(identifier);
  }
}
