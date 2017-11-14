package org.cdlib.cursive.api;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

// TODO: rewrite as nested if/as/when there's a vertx-junit5: https://github.com/vert-x3/vertx-unit/issues/43
@RunWith(VertxUnitRunner.class)
public abstract class CursiveServerTestBase {
  private Vertx vertx;
  private int httpPort;

  Vertx vertx() {
    return vertx;
  }

  int httpPort() {
    return httpPort;
  }

  @Before
  public void setUp(TestContext tc) {
    httpPort = 8180;
    DeploymentOptions options = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", httpPort()));

    vertx = Vertx.vertx();

    // TODO: is there a better way to do this than Class.getName()?
    vertx().deployVerticle(
      CursiveServer.class.getName(),
      options,
      tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx().close(tc.asyncAssertSuccess());
  }
}
