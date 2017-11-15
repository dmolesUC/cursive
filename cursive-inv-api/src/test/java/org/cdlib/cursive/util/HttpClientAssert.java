package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import org.assertj.core.api.AbstractAssert;

import java.net.URI;
import java.util.Objects;

public class HttpClientAssert extends AbstractAssert<HttpClientAssert, HttpClient> {

  private final TestContext ctx;

  HttpClientAssert(HttpClient client, TestContext ctx) {
    super(client, HttpClientAssert.class);
    this.ctx = ctx;
  }

  public GetAssertions get() {
    return new GetAssertions(this.actual, ctx);
  }

  public GetAssertions get(String url) {
    URI uri = URI.create(url);

    GetAssertions get = get()
      .host(uri.getHost())
      .port(uri.getPort())
      .path(uri.getPath());

    return isSSL(uri) ? get.withSsl() : get;
  }

  private boolean isSSL(URI uri) {
    String scheme = Objects.toString(uri.getScheme()).toLowerCase();
    switch (scheme) {
      case "http":
        return false;
      case "https":
        return true;
      default:
        throw new IllegalArgumentException("URI <" + uri + "> has an unsupported scheme <" + scheme + ">; must be http or https");
    }
  }
}
