package org.cdlib.cursive.api;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CursiveServerTest {
  private Vertx vertx;
  private int httpPort;

  @Before
  public void setUp(TestContext tc) {
    httpPort = 8180;
    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", httpPort));

    vertx = Vertx.vertx();

    // TODO: is there a better way to do this than Class.getName()?
    vertx.deployVerticle(
      CursiveServer.class.getName(),
      options,
      tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void serverStarts(TestContext tc) {
    Async async = tc.async();
    // TODO: fluent assertions
    vertx.createHttpClient().getNow(httpPort, "localhost", "/", response -> {
      tc.assertEquals(response.statusCode(), 200);
      response.bodyHandler(body -> async.complete());
    });
  }
}
