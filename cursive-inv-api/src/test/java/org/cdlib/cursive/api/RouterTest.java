package org.cdlib.cursive.api;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static org.cdlib.cursive.util.TestUtils.getResourceAsString;
import static org.cdlib.cursive.util.VertxAssertions.withContext;

public class RouterTest extends CursiveServerTestBase {
  @Test
  public void getRoot(TestContext tc) {

    String expectedBody = getResourceAsString("routes_root.json");

    HttpClient httpClient = vertx().createHttpClient();
    withContext(tc)
      .assertThat(httpClient).get().host("localhost").port(httpPort()).path("/")
      .response()
      .hasContentType("application/hal+json")
      .hasBody(expectedBody)
    ;
  }

}
