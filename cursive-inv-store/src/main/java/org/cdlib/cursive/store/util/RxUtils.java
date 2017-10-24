package org.cdlib.cursive.store.util;

import io.reactivex.Maybe;
import io.vavr.control.Option;

public class RxUtils {
  public static <T> Maybe<T> toMaybe(Option<T> option) {
    return option.map(Maybe::just).getOrElse(Maybe.empty());
  }
}
