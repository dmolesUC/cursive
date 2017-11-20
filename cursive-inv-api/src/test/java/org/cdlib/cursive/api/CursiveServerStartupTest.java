package org.cdlib.cursive.api;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class CursiveServerStartupTest extends CursiveServerTestBase {

  @Test
  public void serverStarts(TestContext tc) {
    Async async = tc.async();
    HttpClient httpClient = vertx().createHttpClient();
    httpClient.getNow(httpPort(), "localhost", "/", r -> {
        tc.assertTrue(r.statusCode() == 200);
        async.complete();
      }
    );
  }
}
