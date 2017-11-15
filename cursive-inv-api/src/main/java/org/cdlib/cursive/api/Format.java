package org.cdlib.cursive.api;

import io.vavr.collection.Array;
import io.vavr.control.Option;

import java.util.Objects;

public enum Format {

  HAL("application/hal+json"),
  JSON_LD("application/ld+json");

  public static final String HAL_MIME_TYPE = HAL.contentType;
  public static final String JSON_LD_MIME_TYPE = JSON_LD.contentType;

  public static Format DEFAULT = HAL;

  private final String contentType;

  Format(String contentType) {
    this.contentType = contentType;
  }

  public String contentType() {
    return contentType;
  }

  public static Array<Format> all() {
    return Array.of(values());
  }

  public static Option<Format> byContentType(String contentType) {
    return all().find(ct -> Objects.equals(ct.contentType, contentType));
  }

}
