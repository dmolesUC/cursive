package org.cdlib.kufi.util;

public interface Ordered<T> extends Comparable<T> {
  default boolean lessThan(T other) {
    return this.compareTo(other) < 0;
  }

  default boolean lessThanOrEqualTo(T other) {
    return this.compareTo(other) <= 0;
  }

  default boolean greaterThan(T other) {
    return this.compareTo(other) > 0;
  }

  default boolean greaterThanOrEqualTo(T other) {
    return this.compareTo(other) >= 0;
  }
}

