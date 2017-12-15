package org.cdlib.cursive.core;

public interface Resource {
  String identifier();
  ResourceType type();

  static String toString(Resource r) {
    return r.getClass().getName() + "<" + r.identifier() + ">";
  }
}
