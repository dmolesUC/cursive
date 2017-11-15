package org.cdlib.cursive.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static org.cdlib.cursive.util.TestUtils.getResourceAsString;
import static org.cdlib.cursive.util.VertxAssertions.withContext;

public class RouterTest extends CursiveServerTestBase {
  @Test
  public void getRootAsHal(TestContext tc) {
    String expectedBody = getResourceAsString("routes_root_hal.json");

    HttpClient httpClient = vertx().createHttpClient();
    withContext(tc)
      .assertThat(httpClient).get().host("localhost").port(httpPort()).path("/")
      .withHeader(HttpHeaderNames.ACCEPT, "application/hal+json")
      .response()
      .hasContentType("application/hal+json")
      .hasBody(expectedBody)
    ;
  }

  @Test
  public void getRootAsJsonLD(TestContext tc) {
    String expectedBody = getResourceAsString("routes_root_json-ld.json");

    HttpClient httpClient = vertx().createHttpClient();
    withContext(tc)
      .assertThat(httpClient).get().host("localhost").port(httpPort()).path("/")
      .withHeader(HttpHeaderNames.ACCEPT, "application/ld+json")
      .response()
      .hasContentType("application/ld+json")
      .hasBody(expectedBody)
    ;
  }
}
