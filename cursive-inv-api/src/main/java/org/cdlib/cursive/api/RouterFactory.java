package org.cdlib.cursive.api;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import org.cdlib.cursive.core.async.AsyncStore;

public class RouterFactory {
  private final AsyncStore store;

  public RouterFactory(AsyncStore store) {
    this.store = store;
  }

  public Router create(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route().handler(ctx -> ctx.response()
      .end("Hello"));
    return router;
  }
}
