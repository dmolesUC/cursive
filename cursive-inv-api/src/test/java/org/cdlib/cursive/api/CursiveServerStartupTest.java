package org.cdlib.cursive.api;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class CursiveServerStartupTest extends CursiveServerTestBase {
  @Test
  public void serverStarts(TestContext tc) {
    Async async = tc.async();
    // TODO: fluent assertions
    vertx().createHttpClient().getNow(httpPort(), "localhost", "/", response -> {
      tc.assertEquals(response.statusCode(), 200);
      response.bodyHandler(body -> async.complete());
    });
  }
}
