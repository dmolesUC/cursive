package org.cdlib.cursive.util;

import io.vavr.Value;
import org.assertj.core.api.AbstractAssert;

public class ValueAssert<T> extends AbstractAssert<ValueAssert<T>, Value<T>> {
  public ValueAssert(Value<T> ts) {
    super(ts, ValueAssert.class);
  }

  static <T> ValueAssert<T> assertThat(Value<T> actual) {
    return new ValueAssert<>(actual);
  }

  public ValueAssert<T> isEmpty() {
    if (actual == null) {
      failWithMessage("Exected empty value, but found null instead");
    }
    if (!actual.isEmpty()) {
      failWithMessage("Expected value to be empty, but contained <%s>", actual.get());
    }
    return this;
  }

  public ValueAssert<T> contains(T value) {
    if (actual == null) {
      failWithMessage("Exected value to contain <%s>, but value was null", value);
    }
    if (!actual.contains(value)) {
      if (actual.isEmpty()) {
        failWithMessage("Expected value to contain <%s>, but was empty", value);
      } else {
        failWithMessage("Expected value to contain <%s>, but contained <%s>", value, actual.get());
      }
    }
    return this;
  }
}
