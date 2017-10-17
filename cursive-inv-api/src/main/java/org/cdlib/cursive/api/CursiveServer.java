package org.cdlib.cursive.api;

import io.vertx.core.AbstractVerticle;

public class CursiveServer extends AbstractVerticle {

  public static final int DEFAULT_CURSIVE_PORT = 8080;

  @Override
  public void start() throws Exception {
    int cursivePort = Integer.getInteger("cursive.port", DEFAULT_CURSIVE_PORT);
    vertx.createHttpServer()
      .requestHandler(r -> r.response().end("Hello"))
      .listen(cursivePort);
  }
}
