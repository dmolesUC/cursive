package org.cdlib.cursive.util;

import io.vavr.collection.List;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

public class RequestAssert extends AbstractAssert<RequestAssert, HttpClientRequest> {

  private List<Handler<HttpClientResponse>> responseHandlers = List.empty();
  private List<Handler<Buffer>> bodyHandlers = List.empty();

  private final TestContext tc;

  RequestAssert(TestContext tc, HttpClientRequest request) {
    super(request, RequestAssert.class);
    this.tc = tc;

    Async async = tc.async();
    request.handler(response -> {
      responseHandlers.forEach(h -> h.handle(response));
      response.bodyHandler(body -> bodyHandlers.forEach(h -> h.handle(body)));
      async.complete();
    });
  }

  private void addResponseHandler(Handler<HttpClientResponse> h) {
    responseHandlers = responseHandlers.append(h);
  }

  private void addBodyHandler(Handler<Buffer> h) {
    bodyHandlers = bodyHandlers.append(h);
  }

  public RequestAssert receivedStatus(int expectedStatus) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    addResponseHandler(response -> {
      int actualStatus = response.statusCode();
      tc.assertEquals(
        expectedStatus,
        actualStatus,
        String.format("Expected status %d, got %d", expectedStatus, actualStatus)
      );
    });

    return this;
  }

  public RequestAssert receivedContentType(String expectedType) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    addResponseHandler(response -> {
      String actualType = response.getHeader(CONTENT_TYPE);
      tc.assertEquals(
        expectedType,
        actualType,
        String.format(
          "Expected %s ‹%s›, got ‹%s›",
          CONTENT_TYPE,
          expectedType,
          actualType
        ));
    });

    return this;
  }

  public RequestAssert receivedBodyJson(String expectedBody) {
    if (actual == null) {
      failWithMessage("Expected HttpClientRequest, but found null instead");
      return this;
    }

    addBodyHandler(body -> {
      String actualBody = body.toString();
      try {
        assertJsonEquals(expectedBody, actualBody);
      } catch (Throwable t) {
        System.err.printf("Expected: <%s>%n", expectedBody);
        System.err.printf("Actual:   <%s>%n", actualBody);
        tc.fail(t);
      }
    });

    return this;
  }

}
