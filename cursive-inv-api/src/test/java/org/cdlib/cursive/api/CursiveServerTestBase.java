package org.cdlib.cursive.api;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

// TODO: rewrite as nested if/as/when there's a vertx-junit5: https://github.com/vert-x3/vertx-unit/issues/43
@RunWith(VertxUnitRunner.class)
public abstract class CursiveServerTestBase {

  private volatile Vertx vertx;
  private int httpPort;

  Vertx vertx() {
    return vertx;
  }

  int httpPort() {
    return httpPort;
  }

  private int findOpenPort() {
    try {
      try (ServerSocket s = new ServerSocket(0)) {
        return s.getLocalPort();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Before
  public void setUp(TestContext tc) {
    httpPort = findOpenPort();

    DeploymentOptions deploymentOptions = new DeploymentOptions()
      .setConfig(new JsonObject().put("http.port", httpPort));
    vertx = Vertx.vertx();

    vertx.deployVerticle(
      CursiveServer::new,
      deploymentOptions,
      tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

}
