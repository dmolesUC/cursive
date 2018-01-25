package org.cdlib.cursive.api;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.cursive.api.s11n.HalSerializer;
import org.cdlib.cursive.api.s11n.JsonLdSerializer;
import org.cdlib.cursive.api.s11n.LinkedResult;
import org.cdlib.cursive.api.s11n.Serializer;
import org.cdlib.cursive.core.async.AsyncStore;

import java.util.Objects;

import static org.cdlib.cursive.api.s11n.Cursive.WORKSPACES;

public enum Format {

  HAL("application/hal+json", new HalSerializer()),
  JSON_LD("application/ld+json", new JsonLdSerializer());

  public static final Format DEFAULT = HAL;

  private final String contentType;
  private final Serializer serializer;

  Format(String contentType, Serializer serializer) {
    this.contentType = contentType;
    this.serializer = serializer;
  }

  public String contentType() {
    return contentType;
  }

  public String format(AsyncStore store) {
    return serializer.toString(toResult(store));
  }

  public static Array<Format> all() {
    return Array.of(values());
  }

  public static Option<Format> byContentType(String contentType) {
    return all().find(ct -> Objects.equals(ct.contentType, contentType));
  }

  private static LinkedResult toResult(AsyncStore s) {
    return new LinkedResult("/")
      .withLink(WORKSPACES, "workspaces");
  }

}
