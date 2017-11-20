package org.cdlib.cursive.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static org.cdlib.cursive.util.RequestBuilder.withClient;
import static org.cdlib.cursive.util.TestUtils.getResourceAsString;
import static org.cdlib.cursive.util.VertxAssertions.inContext;

public class RouterTest extends CursiveServerTestBase {

  private void makeRequest(TestContext tc, String requestedType, String expectedBody) {
    HttpClientRequest request =
      withClient(vertx().createHttpClient())
        .withHeader(HttpHeaderNames.ACCEPT, requestedType)
        .get().host("localhost").port(httpPort()).path("/")
        .makeRequest();

    inContext(tc)
      .assertThat(request)
      .receivedStatus(200)
      .receivedContentType(requestedType)
      .receivedBody(expectedBody);

    request.end();
  }

  @Test
  public void getRootAsHal(TestContext tc) {
    String requestedType = "application/hal+json";
    String expectedBody = getResourceAsString("routes_root_hal.json");
    makeRequest(tc, requestedType, expectedBody);
  }

  @Test
  public void getRootAsJsonLd(TestContext tc) {
    String expectedType = "application/hal+json";
    String expectedBody = getResourceAsString("routes_root_json-ld.json");
    makeRequest(tc, expectedType, expectedBody);
  }
}
