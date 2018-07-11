package org.cdlib.kufi.util;

public interface Ordered<T> extends Comparable<T> {
  default boolean lessThan(T other) {
    return compareTo(other) < 0;
  }

  default boolean greaterThan(T other) {
    return compareTo(other) > 0;
  }

}

