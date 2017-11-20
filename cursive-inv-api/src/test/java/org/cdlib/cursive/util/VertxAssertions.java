package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.Assertions;

public final class VertxAssertions extends Assertions {

  public static final long TIMEOUT_MILLIS = 5000L;

  private final TestContext tc;

  private VertxAssertions(TestContext tc) {
    this.tc = tc;
  }

  public static VertxAssertions inContext(TestContext tc) {
    return new VertxAssertions(tc);
  }

  public RequestAssert assertThat(HttpClientRequest request) {
    return new RequestAssert(tc, request);
  }
}


