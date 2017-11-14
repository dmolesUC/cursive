package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.Assertions;

public class VertxAssertions extends Assertions {

  public static final int DEFAULT_TIMEOUT_MILLIS = 500;

  public static TestContextAssertions withContext(TestContext ctx) {
    return new TestContextAssertions(ctx);
  }

  public static class TestContextAssertions {

    private final TestContext ctx;

    private TestContextAssertions(TestContext ctx) {
      this.ctx = ctx;
    }

    public HttpClientAssert assertThat(HttpClient client) {
      return new HttpClientAssert(client, ctx);
    }

    public ResponseAssert assertThat(HttpClientResponse actual) {
      return new ResponseAssert(actual, ctx);
    }
  }
}

