package com.rosieapp.services.common.model.identifiers;

import java.util.Optional;
import org.apache.commons.lang.math.NumberUtils;

/**
 * A model identifier that uses traditional, long integer primary keys -- typically issued by a
 * single source of record -- to unique identify a persisted model.
 */
public final class LongIdentifier extends PersistedModelIdentifier {
  /**
   * The underlying identifier value.
   */
  private long value;

  /**
   * Attempts to parse the provided string as a long integer model identifier.
   *
   * If the string cannot be parsed, an empty {@link Optional} is returned.
   *
   * @param   value
   *          The string to attempt to parse as a model identifier.
   *
   * @return  Either a {@code Optional} that contains the {@code LongIdentifier} that was populated
   *          by interpreting the provided identifier value; or, an empty {@code Optional} that
   *          signifies that the provided string is not a long integer identifier value.
   */
  public static Optional<ModelIdentifier> createFrom(final String value) {
    final Optional<ModelIdentifier> result;
    final long                      numberValue = NumberUtils.toLong(value, -1);

    if (numberValue != -1) {
      result = Optional.of(new LongIdentifier(numberValue));
    } else {
      result = Optional.empty();
    }

    return result;
  }

  /**
   * Constructor for {@link LongIdentifier}.
   *
   * @see     ModelIdentifierFactory#createIdFrom(String)
   *
   * @param   value
   *          The long integer value to wrap in the new identifier object.
   *          This value must be greater than {@code 0}.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is less than or equal to {@code 0}.
   */
  public LongIdentifier(final long value)
  throws IllegalArgumentException {
    this.setValue(value);
  }

  /**
   * Gets the long integer value inside this identifier object.
   *
   * @return The identifier value.
   */
  public long getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return Long.toString(this.getValue());
  }

  /**
   * Sets the value inside this identifier.
   *
   * @param   value
   *          The long integer value to set inside this object.
   *          This value must be greater than {@code 0}.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is less than or equal to {@code 0}.
   */
  private void setValue(final long value)
  throws IllegalArgumentException {
    if (value <= 0) {
      throw new IllegalArgumentException("value must be greater than 0");
    }

    this.value = value;
  }
}