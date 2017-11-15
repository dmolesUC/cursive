package org.cdlib.cursive.util;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import io.vavr.Lazy;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.unit.TestContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

public class GetAssertions {

  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 8080;
  public static final String DEFAULT_PATH = "/";

  private final HttpClient client;
  private final TestContext ctx;
  private final RequestOptions requestOptions;
  private final VertxHttpHeaders headers = new VertxHttpHeaders();

  private final Lazy<HttpClientResponse> response = Lazy.of(this::getResponse);

  private long timeoutMillis;

  GetAssertions(HttpClient client, TestContext ctx) {
    this.client = client;
    this.ctx = ctx;
    this.timeoutMillis = VertxAssertions.DEFAULT_TIMEOUT_MILLIS;

    CharSequence headerName = ACCEPT;
    String headerValue = "application/hal+json";



    requestOptions = new RequestOptions()
      .setPort(DEFAULT_PORT)
      .setHost(DEFAULT_HOST)
      .setURI(DEFAULT_PATH);
  }

  public GetAssertions host(String host) {
    requestOptions.setHost(host);
    return this;
  }

  public GetAssertions port(int port) {
    requestOptions.setPort(port);
    return this;
  }

  public GetAssertions path(String path) {
    requestOptions.setURI(path);
    return this;
  }

  public GetAssertions withSsl() {
    requestOptions.setSsl(true);
    return this;
  }

  public GetAssertions withTimeout(long millis) {
    this.timeoutMillis = millis;
    return this;
  }

  public GetAssertions withHeader(CharSequence headerName, CharSequence headerValue) {
    headers.add(headerName, headerValue);
    return this;
  }

  /**
   * Blocks until response received.
   */
  public ResponseAssert response() {
    return new ResponseAssert(response.get(), ctx, timeoutMillis);
  }

  private HttpClientResponse getResponse() {
    CompletableFuture<HttpClientResponse> resultFuture = makeRequest();

    HttpClientResponse response = null;
    try {
      response = resultFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      ctx.fail(e);
    }
    return response;
  }

  private CompletableFuture<HttpClientResponse> makeRequest() {
    CompletableFuture<HttpClientResponse> resultFuture = new CompletableFuture<>();
    try {
      HttpClientRequest request = client.get(requestOptions);
      request.headers().addAll(headers);
      request.setTimeout(timeoutMillis);
      request.handler(resultFuture::complete);
      request.exceptionHandler(resultFuture::completeExceptionally);
      request.end();
    } catch (Throwable t) {
      resultFuture.completeExceptionally(t);
    }
    return resultFuture;
  }

}
