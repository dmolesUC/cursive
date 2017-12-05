package org.cdlib.cursive.api;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import org.cdlib.cursive.core.async.AsyncStore;
import org.cdlib.cursive.store.memory.async.AsyncMemoryStore;

public class CursiveServer extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    AsyncStore store = new AsyncMemoryStore();
    RouterFactory routerFactory = new RouterFactory(store);
    Router router = routerFactory.create(vertx);

    int httpPort = config().getInteger("http.port");
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(httpPort);
  }
}
