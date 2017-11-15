package org.cdlib.cursive.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Option;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.MIMEHeader;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.cdlib.cursive.core.async.AsyncStore;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.vavr.API.Some;

public class RouterFactory {

  private final AsyncStore store;

  public RouterFactory(AsyncStore store) {
    this.store = store;
  }

  public Router create(Vertx vertx) {
    Router router = Router.router(vertx);

    Route route = router.route();
    route = Format.all().foldLeft(route, (r, f) -> r.produces(f.contentType()));
    route.handler(RouterFactory::getRoot);

    return router;
  }

  private static void getRoot(RoutingContext ctx) {
    Option<Format> acceptedFormat = getAcceptedFormat(ctx);
    acceptedFormat
      .onEmpty(() -> ctx.response()
        .setStatusCode(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE.code())
        .end())
      .forEach(fmt -> ctx.response()
        .putHeader(CONTENT_TYPE.toString(), fmt.contentType())
        .end("Hello"));
  }

  private static Option<Format> getAcceptedFormat(RoutingContext ctx) {
    List<MIMEHeader> acceptedTypes = ctx.parsedHeaders().accept();
    if (acceptedTypes.isEmpty()) {
      return Some(Format.DEFAULT);
    }
    return Format.byContentType(ctx.getAcceptableContentType());
  }

}
