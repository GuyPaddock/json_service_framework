package com.rosieapp.services.common.model.identification;

import java.util.Objects;
import java.util.Optional;

/**
 * A model identifier that uses an opaque string to unique identify a persisted model.
 */
public final class StringIdentifier extends PersistedModelIdentifier {
  /**
   * The underlying identifier value.
   */
  private String value;

  /**
   * Attempts to parse the provided string as an identifier.
   *
   * @param   value
   *          The string to attempt to parse as an identifier.
   *
   * @return  Either a {@code Optional} that contains the {@code StringIdentifier} that was
   *          populated by interpreting the provided value as a string identifier; or, an empty
   *          {@code Optional} that signifies that the provided string is not a valid identifier
   *          value.
   */
  public static Optional<ModelIdentifier> createFrom(final String value) {
    final Optional<ModelIdentifier> result;

    if (value == null) {
      result = Optional.empty();
    }
    else {
      result = Optional.of(new StringIdentifier(value));
    }

    return result;
  }

  /**
   * Constructor for {@link StringIdentifier}.
   *
   * @see     ModelIdentifierFactory#createIdFrom(String)
   *
   * @param   value
   *          The string to wrap in the new identifier object.
   *          This value must not be {@code null}.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is {@code null}.
   */
  public StringIdentifier(final String value)
  throws IllegalArgumentException {
    this.setValue(value);
  }

  /**
   * Gets the string value inside this identifier object.
   *
   * @return The identifier value.
   */
  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.getValue();
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
      StringIdentifier otherId = (StringIdentifier)other;

      result = Objects.equals(this.getValue(), otherId.getValue());
    }

    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getValue());
  }

  /**
   * Sets the value inside this identifier.
   *
   * @param   value
   *          The string to set inside this object.
   *          This value must not be {@code null}.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is {@code null}.
   */
  private void setValue(final String value)
  throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }

    this.value = value;
  }
}
