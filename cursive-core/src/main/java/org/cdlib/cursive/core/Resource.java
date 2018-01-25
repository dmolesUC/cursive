package org.cdlib.cursive.core;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public interface Resource {
  UUID id();

  ResourceType type();

  String parentPath();

  default String slug() {
    return id().toString();
  }

  default String path() {
    return StringUtils.removeEnd(parentPath(), "/")
      + "/"
      + type().collectivePath()
      + "/"
      + slug();
  }
}
