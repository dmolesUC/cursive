package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class RequestAssert extends AbstractAssert<RequestAssert, HttpClientRequest> {
  private final TestContext tc;

  RequestAssert(TestContext tc, HttpClientRequest request) {
    super(request, RequestAssert.class);
    this.tc = tc;
  }

  public RequestAssert receivedStatus(int expectedStatus) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    Async async = tc.async();
    actual.handler(r -> {
      int actualStatus = r.statusCode();
      tc.assertEquals(
        expectedStatus,
        actualStatus,
        String.format("Expected status %d, got %d", expectedStatus, actualStatus)
      );
      async.complete();
    });

    return this;
  }

  public RequestAssert receivedContentType(String expectedType) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    Async async = tc.async();
    actual.handler(r -> {
      String actualType = r.getHeader(CONTENT_TYPE);
      tc.assertEquals(
        expectedType,
        actualType,
        String.format(
          "Expected %s ‹%s›, got ‹%s›",
          CONTENT_TYPE,
          expectedType,
          actualType
        ));
      async.complete();
    });

    return this;
  }

  public RequestAssert receivedBody(String expectedBody) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    Async async = tc.async();
    actual.handler(r -> r.bodyHandler(b -> {
        String actualBody = b.toString();
        tc.assertEquals(
          expectedBody,
          actualBody,
          String.format("Expected body ‹%s›, got ‹%s›",
            expectedBody,
            actualBody)
        );
        async.complete();
      }
      )
    );

    return this;
  }
}
