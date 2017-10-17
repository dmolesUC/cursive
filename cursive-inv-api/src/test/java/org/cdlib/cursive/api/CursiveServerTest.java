package org.cdlib.cursive.api;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.cdlib.cursive.api.CursiveServer.DEFAULT_CURSIVE_PORT;

@RunWith(VertxUnitRunner.class)
public class CursiveServerTest {
  private Vertx vertx;
  private int cursivePort;

  @Before
  public void setUp(TestContext tc) {
    // TODO: figure out how to share IDEA/Gradle test configuration
    cursivePort = Integer.getInteger("cursive.port", DEFAULT_CURSIVE_PORT);
//    cursivePort = Integer.getInteger("cursive.port");

    vertx = Vertx.vertx();

    // TODO: is there a better way to do this than Class.getName()?
    vertx.deployVerticle(CursiveServer.class.getName(), tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void serverStarts(TestContext tc) {
    Async async = tc.async();
    // TODO: fluent assertions
    vertx.createHttpClient().getNow(cursivePort, "localhost", "/", response -> {
      tc.assertEquals(response.statusCode(), 200);
      response.bodyHandler(body -> async.complete());
    });
  }
}
