package org.cdlib.cursive.util;

import io.vavr.collection.List;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.StringAssert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseAssert extends AbstractAssert<ResponseAssert, HttpClientResponse> {

  private final TestContext ctx;
  private final long timeoutMillis;

  ResponseAssert(HttpClientResponse response, TestContext ctx, long timeoutMillis) {
    super(response, ResponseAssert.class);
    this.ctx = ctx;
    this.timeoutMillis = timeoutMillis;
  }

  public ResponseAssert hasStatus(int expectedStatus) {
    if (actual == null) {
      failWithMessage("Expected Response, but found null instead");
      return this;
    }
    int actualStatus = actual.statusCode();
    ctx.assertEquals(
      expectedStatus,
      actualStatus,
      String.format(
        "Expected status code ‹%d›, got ‹%d›",
        expectedStatus,
        actualStatus
      ));
    return this;
  }

  public ResponseAssert hasContentType(String expectedType) {
    if (actual == null) {
      failWithMessage("Expected Response, but found null instead");
      return this;
    }
    String actualType = actual.getHeader(CONTENT_TYPE);
    ctx.assertEquals(
      expectedType,
      actualType,
      String.format(
        "Expected %s ‹%s›, got ‹%s›",
        CONTENT_TYPE,
        expectedType,
        actualType
      ));
    return this;
  }

  public ResponseAssert hasBody(String expectedBody) {
    if (actual == null) {
      failWithMessage("Expected Response, but found null instead");
      return this;
    }
    String actualBody = getBody();

    assertThat(actualBody)
      .withFailMessage(
        "Expected body ‹%s›, got ‹%s›",
        expectedBody,
        actualBody
      )
      .isEqualToIgnoringWhitespace(expectedBody);
    return this;
  }

  private String getBody() {
    CompletableFuture<Buffer> bodyFuture = new CompletableFuture<>();
    try {
      actual.bodyHandler(bodyFuture::complete);
    } catch (Throwable t) {
      bodyFuture.completeExceptionally(t);
    }

    Buffer body = null;
    try {
      body = bodyFuture.get(timeoutMillis, MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      ctx.fail(e);
    }
    ctx.assertNotNull(body, "No body returned; expected " + Buffer.class.getName() + ", got null");
    return body.toString();
  }

}
