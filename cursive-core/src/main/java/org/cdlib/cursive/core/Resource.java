package org.cdlib.cursive.core;

import java.util.UUID;

public interface Resource {
  String path();
  UUID id();
  ResourceType type();

  static String toString(Resource r) {
    return r.getClass().getName() + "<" + r.id() + " (" + r.path() + ")>";
  }
}
