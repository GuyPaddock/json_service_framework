package com.rosieapp.services.common.model.identification;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class LongIdentifierSerializer
  extends StdSerializer<LongIdentifier> {

  public LongIdentifierSerializer() {
    this(null);
  }

  public LongIdentifierSerializer(Class<LongIdentifier> t) {
    super(t);
  }

  @Override
  public void serialize(LongIdentifier longIdentifier, JsonGenerator jsonGenerator,
    SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeNumber(longIdentifier.getValue());
  }
}
