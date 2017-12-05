package org.cdlib.cursive.store.util;

import io.reactivex.Maybe;
import io.vavr.control.Option;
import io.vavr.control.Try;

// TODO: explicit tests for these
public class RxUtils {
  public static <T> Maybe<T> toMaybe(Option<T> option) {
    return option.map(Maybe::just).getOrElse(Maybe.empty());
  }

  public static <T> Maybe<T> toMaybe(Try<T> t) {
    Try<Maybe<T>> t1 = t.map(Maybe::just);
    return t1.getOrElseGet(Maybe::error);
  }
}
