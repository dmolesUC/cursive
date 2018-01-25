package org.cdlib.cursive.core;

import io.vavr.Lazy;

// TODO: should Store be a resource?
public enum ResourceType {
  WORKSPACE,
  COLLECTION,
  OBJECT,
  FILE;

  private final Lazy<String> collectivePath = Lazy.of(() -> name().toLowerCase() + "s");

  public String collectivePath() {
    return collectivePath.get();
  }
}
