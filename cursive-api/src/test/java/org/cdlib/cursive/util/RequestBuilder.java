package org.cdlib.cursive.util;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;

import static org.cdlib.cursive.util.VertxAssertions.TIMEOUT_MILLIS;

public class RequestBuilder {
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 8080;
  public static final String DEFAULT_PATH = "/";

  private final HttpClient client;
  private final RequestOptions requestOptions;
  private final VertxHttpHeaders headers = new VertxHttpHeaders();

  private HttpMethod method = HttpMethod.GET;

  private RequestBuilder(HttpClient client) {
    this.client = client;
    requestOptions = new RequestOptions()
      .setPort(DEFAULT_PORT)
      .setHost(DEFAULT_HOST)
      .setURI(DEFAULT_PATH);
  }

  public static RequestBuilder withClient(HttpClient client) {
    return new RequestBuilder(client);
  }

  public RequestBuilder get() {
    method = HttpMethod.GET;
    return this;
  }

  public RequestBuilder host(String host) {
    requestOptions.setHost(host);
    return this;
  }

  public RequestBuilder port(int port) {
    requestOptions.setPort(port);
    return this;
  }

  public RequestBuilder path(String path) {
    requestOptions.setURI(path);
    return this;
  }

  public RequestBuilder withSsl() {
    requestOptions.setSsl(true);
    return this;
  }

  public RequestBuilder withHeader(CharSequence headerName, CharSequence headerValue) {
    headers.add(headerName, headerValue);
    return this;
  }

  public HttpClientRequest makeRequest() {
    HttpClientRequest request = client.request(method, requestOptions);
    request.headers().addAll(headers);
    request.setTimeout(TIMEOUT_MILLIS);
    return request;
  }
}
