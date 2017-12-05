package org.cdlib.cursive.util;

import io.vavr.Value;
import org.assertj.core.api.Assertions;

public class VavrAssertions extends Assertions {
  public static <T> ValueAssert<T> assertThat(Value<T> actual) {
    return ValueAssert.assertThat(actual);
  }
}
