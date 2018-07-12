package org.cdlib.kufi;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public enum LinkType {
  PARENT_OF, CHILD_OF;

  public static Seq<LinkType> allTypes() {
    return Stream.of(values());
  }
}
