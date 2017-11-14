package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpClientAssert extends AbstractAssert<HttpClientAssert, HttpClient> {

  private final TestContext ctx;

  HttpClientAssert(HttpClient client, TestContext ctx) {
    super(client, HttpClientAssert.class);
    this.ctx = ctx;
  }

  public GetAssertions getNow(int port, String host, String path) {
    return new GetAssertions(this.actual, ctx, port, host, path, VertxAssertions.DEFAULT_TIMEOUT_MILLIS);
  }

  public GetAssertions getNow(int port, String host, String path, long timeoutMillis) {
    return new GetAssertions(this.actual, ctx, port, host, path, timeoutMillis);
  }

  public static class GetAssertions {

    private final TestContext ctx;
    private final long timeoutMillis;
    private final CompletableFuture<HttpClientResponse> resultFuture = new CompletableFuture<>();

    GetAssertions(HttpClient client, TestContext ctx, int port, String host, String path, long timeoutMillis) {
      this.ctx = ctx;
      this.timeoutMillis = timeoutMillis;
      try {
        client.getNow(port, host, path, resultFuture::complete);
      } catch (Throwable t) {
        resultFuture.completeExceptionally(t);
      }
    }

    /**
     * Blocks until response received.
     */
    public ResponseAssert response() {
      HttpClientResponse response = null;
      try {
        response = resultFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        ctx.fail(e);
      }
      return new ResponseAssert(response, ctx);
    }
  }

}
