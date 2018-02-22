/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */
package com.rosieapp.services.common.model.identification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * A convenient factory for transforming values that represent model identifier values into their
 * corresponding {@link ModelIdentifier} instances.
 *
 * <p>This factory is a singleton. Use {@link #getInstance()} to get a reference to the factory
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
    Collections.unmodifiableList(
      Arrays.asList(
        LongIdentifier::createFrom,
        UUIDIdentifier::createFrom,
        StringIdentifier::createFrom
      ));

  /**
   * Gets the singleton instance of this factory.
   *
   * @return  The instance of this factory.
   */
  public static ModelIdentifierFactory getInstance() {
    return INSTANCE;
  }

  /**
   * Convenience method for {@code getInstance().createIdFrom(String)}.
   *
   * @see #createIdFrom(String)
   */
  public static ModelIdentifier valueOf(final String identifier) {
    return getInstance().createIdFrom(identifier);
  }

  /**
   * Convenience method for {@code getInstance().createIdFrom(long)}.
   *
   * @see #createIdFrom(long)
   */
  public static ModelIdentifier valueOf(final long identifier) {
    return getInstance().createIdFrom(identifier);
  }

  /**
   * Convenience method for {@code getInstance().createIdFrom(UUID)}.
   *
   * @see #createIdFrom(UUID)
   */
  public static ModelIdentifier valueOf(final UUID identifier) {
    return getInstance().createIdFrom(identifier);
  }

  /**
   * Attempts to parse the provided string representation of an identifier into the type of model
   * identifier that is most appropriate for the type of identifier the string represents.
   *
   * @param   identifier
   *          The string to attempt to convert into a model identifier.
   *          This value must not be {@code null}.
   *
   * @return  A model identifier that was created by parsing the given identifier string.
   *
   * @throws  NullPointerException
   *          If {@code identifier} is {@code null}.
   * @throws  IllegalArgumentException
   *          If none of the known identifier formats matched the format of the provided identifier
   *          string.
   */
  public ModelIdentifier createIdFrom(final String identifier)
  throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(identifier, "identifier cannot be null");

    return STRATEGIES.stream()
      .map((strategy) -> strategy.apply(identifier))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .orElseThrow(
        () -> new IllegalArgumentException("Unrecognized identifier format: " + identifier));
  }

  /**
   * Converts the specified long integer identifier into a {@link ModelIdentifier} instance.
   *
   * @param   identifier
   *          The identifier to convert into a model identifier.
   *          This value must be greater than {@code 0}.
   *
   * @return  A model identifier that wraps the provided long integer.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is less than or equal to {@code 0}.
   */
  public ModelIdentifier createIdFrom(final long identifier)
  throws IllegalArgumentException {
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
   * @throws  NullPointerException
   *          If {@code identifier} is {@code null}.
   */
  public ModelIdentifier createIdFrom(final UUID identifier)
  throws NullPointerException {
    return new UUIDIdentifier(identifier);
  }
}
