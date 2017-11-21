package com.rosieapp.services.common.model.identification;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A model identifier that uses a Universally Unique Identifier to unique identify a persisted
 * model.
 * <p>
 * A UUID is typically represented in string format like:
 * {@code 82159191-e513-459d-8a2c-c4a5cf45c7a2}
 */
public final class UUIDIdentifier extends PersistedModelIdentifier {
  /**
   * The underlying identifier value.
   */
  private UUID value;

  /**
   * Attempts to parse the provided string as a UUID.
   * <p>
   * If the string cannot be parsed, an empty {@link Optional} is returned.
   *
   * @param   value
   *          The string to attempt to parse as a model identifier.
   *
   * @return  Either a {@code Optional} that contains the {@code {@link UUIDIdentifier}} that was
   *          populated by interpreting the provided identifier value; or, an empty {@code Optional}
   *          that signifies that the provided string is not the string representation of a UUID
   *          value.
   */
  public static Optional<ModelIdentifier> createFrom(final String value) {
    Optional<ModelIdentifier> result;

    try {
      result = Optional.of(new UUIDIdentifier(value));
    }
    catch (IllegalArgumentException ex) {
      result = Optional.empty();
    }

    return result;
  }

  /**
   * Constructor for {@link UUIDIdentifier} that generates a random, new identifier.
   * <p>
   * The generated identifier should be safe to use to identify a new object in any system.
   */
  public UUIDIdentifier() {
    this(UUID.randomUUID());
  }

  /**
   * Constructor for {@link UUIDIdentifier} that populates an identifier by parsing the provided
   * string representation of a UUID.
   *
   * @see     ModelIdentifierFactory#createIdFrom(String)
   *
   * @param   value
   *          The string value to interpret as a UUID that will be wrapped in the new identifier
   *          object. This value must not be {@code null} and must be a valid string representation
   *          of a UUID.
   *
   * @throws  NullPointerException
   *          If {@code value} is {@code null}.
   * @throws  IllegalArgumentException
   *          If {@code value} is not a proper string representation of a {@code UUID}.
   *
   */
  public UUIDIdentifier(final String value)
  throws NullPointerException, IllegalArgumentException {
    this.setValue(value);
  }

  /**
   * Constructor for {@link UUIDIdentifier}.
   *
   * @see     ModelIdentifierFactory#createIdFrom(String)
   *
   * @param   value
   *          This value must not be {@code null}.
   *
   * @throws  NullPointerException
   *          If {@code value} is {@code null}.
   */
  public UUIDIdentifier(final UUID value)
  throws IllegalArgumentException {
    this.setValue(value);
  }

  /**
   * Gets the UUID value inside this identifier object.
   *
   * @return The identifier value.
   */
  public UUID getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.getValue().toString();
  }

  @Override
  public boolean equals(Object other) {
    final boolean result;

    if (this == other) {
      result = true;
    }
    else if ((other == null) || (this.getClass() != other.getClass())) {
      result = false;
    }
    else {
      UUIDIdentifier otherId = (UUIDIdentifier)other;

      result = Objects.equals(this.getValue(), otherId.getValue());
    }

    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getValue());
  }

  /**
   * Sets the value inside this identifier by parsing the provided string representation of a UUID.
   *
   * @param   value
   *          The string value to interpret as a UUID that will be set inside this object. This
   *          value must not be {@code null} and must be a valid string representation of a UUID.
   *
   * @throws  NullPointerException
   *          If {@code value} is {@code null}.
   * @throws  IllegalArgumentException
   *          If {@code value} is not a proper string representation of a {@code UUID}.
   */
  private void setValue(final String value)
  throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(value, "value cannot be null");

    this.setValue(UUID.fromString(value));
  }

  /**
   * Sets the value inside this identifier.
   *
   * @param   value
   *          The UUID value to set inside this object.
   *          This value must not be {@code null}.
   *
   * @throws  NullPointerException
   *          If {@code value} is {@code null}.
   */
  private void setValue(final UUID value)
  throws NullPointerException {
    Objects.requireNonNull(value, "value cannot be null");

    this.value = value;
  }
}
