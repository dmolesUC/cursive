package org.cdlib.kufi.util;

import io.vavr.collection.Array;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Preconditions {

  public static <T> T checkArgument(T value, Predicate<T> condition, Supplier<String> msgSupplier) {
    require(condition.test(value), msgSupplier);
    return value;
  }

  public static void checkArgument(boolean condition, String fmt, Object... args) {
    require(condition, () -> String.format(fmt, args));
  }

  public static void checkArgument(boolean condition, String fmt, Supplier... args) {
    require(condition, () -> String.format(fmt, Array.of(args).map(Supplier::get).toJavaArray()));
  }

  public static void require(boolean condition, Supplier<String> msgSupplier) {
    if (!condition) {
      throw new IllegalArgumentException(msgSupplier.get());
    }
  }

  private Preconditions() {
    // private to prevent instantiation
  }
}
