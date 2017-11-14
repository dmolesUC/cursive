package org.cdlib.cursive.api;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static org.cdlib.cursive.util.VertxAssertions.withContext;

public class CursiveServerStartupTest extends CursiveServerTestBase {

  @Test
  public void serverStarts(TestContext tc) {
    HttpClient httpClient = vertx().createHttpClient();
    withContext(tc)
      .assertThat(httpClient)
      .getNow(httpPort(), "localhost", "/")
      .response()
      .hasStatus(200)
    ;
  }
}
