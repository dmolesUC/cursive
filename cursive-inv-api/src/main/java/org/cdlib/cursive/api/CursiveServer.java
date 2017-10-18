package org.cdlib.cursive.api;

import io.vertx.core.AbstractVerticle;

public class CursiveServer extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    int httpPort = config().getInteger("http.port");
    vertx.createHttpServer()
      .requestHandler(r -> r.response().end("Hello"))
      .listen(httpPort);
  }
}
