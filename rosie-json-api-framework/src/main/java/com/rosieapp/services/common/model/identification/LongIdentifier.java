/*
 * Copyright (c) 2017-2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.model.identification;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang.math.NumberUtils;

/**
 * A model identifier that uses traditional, long integer primary keys -- typically issued by a
 * single source of record -- to unique identify a persisted model.
 */
@JsonSerialize(using = LongIdentifier.JsonSerializer.class)
public final class LongIdentifier
extends PersistedModelIdentifier
implements Comparable<LongIdentifier> {
  /**
   * The underlying identifier value.
   */
  private long value;


  /**
   * A value that signifies an unparseable or unknown long identifier.
   *
   * <p>By convention, all long identifiers are positive integers, so this value represents an
   * ID that should never actually appear in a model.
   */
  public static final long UNKNOWN_VALUE = -1;

  /**
   * Attempts to parse the provided string as a long integer model identifier.
   *
   * <p>If the string cannot be parsed, an empty {@link Optional} is returned.
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
    final long                      numberValue = NumberUtils.toLong(value, UNKNOWN_VALUE);

    if (numberValue == UNKNOWN_VALUE) {
      result = Optional.empty();
    } else {
      result = Optional.of(new LongIdentifier(numberValue));
    }

    return result;
  }

  /**
   * Constructor for {@link LongIdentifier}.
   *
   * @param   value
   *          The long integer value to wrap in the new identifier object.
   *          This value must be greater than {@code 0}.
   *
   * @throws  IllegalArgumentException
   *          If {@code value} is less than or equal to {@code 0}.
   *
   * @see     ModelIdentifierFactory#createIdFrom(String)
   */
  public LongIdentifier(final long value)
  throws IllegalArgumentException {
    super();

    this.setValue(value);
  }

  /**
   * Default constructor for {@link LongIdentifier}.
   *
   * <p>This constructor is required by Jackson to properly initialize the object with a value, so
   * that the object can be populated while de-serializing JSON.
   */
  @SuppressWarnings("unused")
  private LongIdentifier() {
    super();

    this.value = UNKNOWN_VALUE;
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

  @Override
  public boolean equals(final Object other) {
    final boolean result;

    if (this == other) {
      result = true;
    } else if ((other == null) || (this.getClass() != other.getClass())) {
      result = false;
    } else {
      final LongIdentifier otherId = (LongIdentifier)other;

      result = (this.getValue() == otherId.getValue());
    }

    return result;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(this.getValue());
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

  @Override
  public int compareTo(final LongIdentifier other) {
    return Long.compare(this.getValue(), other.getValue());
  }

  /**
   * Custom Serializer for Jackson to serialize a {@code LongIdentifier} to and from a {@code long}
   * value.
   *
   * <p>Use the {@code @JsonSerialize(using = LongIdentifierSerializer.class)} annotation on fields
   * or classes that are.
   */
  public static class JsonSerializer
  extends StdSerializer<LongIdentifier> {
    /**
     * Constructor for {@code JsonSerializer}.
     */
    @SuppressWarnings("unused")
    public JsonSerializer() {
      this(null);
    }

    /**
     * Constructor for {@code JsonSerializer}.
     *
     * @param identifierType
     *        The type for which this serializer is being instantiated.
     */
    public JsonSerializer(final Class<LongIdentifier> identifierType) {
      super(identifierType);
    }

    /**
     * Custom JsonSerializer for Jackson to serialize a {@code LongIdentifier} to and from a
     * {@code long} value.
     */
    @Override
    public void serialize(final LongIdentifier longIdentifier, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeNumber(longIdentifier.getValue());
    }
  }
}
