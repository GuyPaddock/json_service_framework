package com.rosieapp.services.common.model.identifiers;

import java.util.Optional;
import org.apache.commons.lang.math.NumberUtils;

public final class LongIdentifier extends ExistingObjectIdentifier {
  private long value;

  public static Optional<ObjectIdentifier> createFrom(final String value) {
    final Optional<ObjectIdentifier>  result;
    final long                        numberValue = NumberUtils.toLong(value, -1);

    if (numberValue != -1) {
      result = Optional.of(new LongIdentifier(numberValue));
    }
    else {
      result = Optional.empty();
    }

    return result;
  }

  public LongIdentifier(final long value) {
    this.setValue(value);
  }

  public long getValue() {
    return this.value;
  }

  private void setValue(final long value) {
    if (value <= 0) {
      throw new IllegalArgumentException("value must be greater than 0");
    }

    this.value = value;
  }

  @Override
  public String toString() {
    return Long.toString(this.getValue());
  }
}
