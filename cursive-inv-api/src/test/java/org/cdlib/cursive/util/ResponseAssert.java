package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

public class ResponseAssert extends AbstractAssert<ResponseAssert, HttpClientResponse> {

  private final TestContext ctx;

  ResponseAssert(HttpClientResponse response, TestContext ctx) {
    super(response, ResponseAssert.class);
    this.ctx = ctx;
  }

  public ResponseAssert hasStatus(int expectedStatus) {
    if (actual == null) {
      failWithMessage("Expected Response, but found null instead");
      return this;
    }
    int actualStatus = actual.statusCode();
    ctx.assertEquals(actualStatus, expectedStatus, String.format("Expected status code %d, got %d", expectedStatus, actualStatus));
    return this;
  }
}
