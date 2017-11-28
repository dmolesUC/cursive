package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

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
      // System.out.println("receivedStatus(): in handler");
      int actualStatus = r.statusCode();
      tc.assertEquals(
        expectedStatus,
        actualStatus,
        String.format("Expected status %d, got %d", expectedStatus, actualStatus)
      );
      async.complete();
      // System.out.println("receivedStatus(): async completed");
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
      // System.out.println("receivedContentType(): in handler");
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
      // System.out.println("receivedContentType(): async completed");
    });

    return this;
  }

  public RequestAssert receivedBodyJson(String expectedBody) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    Async async = tc.async();
    actual.handler(r -> {
        // System.out.println("receivedBodyJson(): in handler");
        r.bodyHandler(b -> {
          // System.out.println("receivedBodyJson(): in bodyHandler");
          String actualBody = b.toString();
          // System.out.println(actualBody);
          try {
            assertJsonEquals(expectedBody, actualBody);
          } catch (Throwable t) {
            tc.fail(t);
          }
          async.complete();
          // System.out.println("receivedBodyJson(): async completed");
        });
      }
    );

    return this;
  }
}
