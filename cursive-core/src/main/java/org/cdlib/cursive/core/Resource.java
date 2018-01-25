package org.cdlib.cursive.core;

import java.util.UUID;

public interface Resource {
  UUID id();

  ResourceType type();

  String parentPath();

  default String slug() {
    return id().toString();
  }

  default String path() {
    return parentPath() + "/" + type().collectivePath() + "/" + slug();
  }
}
