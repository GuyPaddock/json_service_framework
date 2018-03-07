package com.rosieapp.services.common.model.identification;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Custom Serializer for jackson to serialize a {@code LongIdentifier} to
 * a {@code long} value. Add {@code @JsonSerialize(using = LongIdentifierSerializer.class)}
 * to a class to use.
 */
public class LongIdentifierSerializer
  extends StdSerializer<LongIdentifier> {

  public LongIdentifierSerializer() {
    this(null);
  }

  public LongIdentifierSerializer(final Class<LongIdentifier> identifier) {
    super(identifier);
  }

  @Override
  public void serialize(final LongIdentifier longIdentifier, final JsonGenerator jsonGenerator,
                        final SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeNumber(longIdentifier.getValue());
  }
}
