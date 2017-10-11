package org.cdlib.cursive.util;

import io.vavr.Value;
import org.assertj.core.api.AbstractAssert;

public class ValueAssert<T> extends AbstractAssert<ValueAssert<T>, Value<T>> {
  public ValueAssert(Value<T> ts) {
    super(ts, ValueAssert.class);
  }

  public static <T> ValueAssert<T> assertThat(Value<T> actual) {
    return new ValueAssert<>(actual);
  }

  public ValueAssert<T> isEmpty() {
    isNotNull();
    if (!actual.isEmpty()) {
      failWithMessage("Expected value to be empty but contained <%s>", actual.get());
    }
    return this;
  }
}
