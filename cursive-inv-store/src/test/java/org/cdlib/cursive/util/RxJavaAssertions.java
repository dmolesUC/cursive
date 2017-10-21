package org.cdlib.cursive.util;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.vavr.Value;
import org.assertj.core.api.Assertions;

public class RxJavaAssertions extends Assertions {
  public static <T> ObservableAssert<T> assertThat(Observable<T> actual) {
    return ObservableAssert.assertThat(actual);
  }
}
