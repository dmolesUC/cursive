package org.cdlib.cursive.util;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.assertj.core.api.AbstractAssert;

public class ObservableAssert<T> extends AbstractAssert<ObservableAssert<T>, Observable<T>> {

  private final TestObserverAssert<T> observer;

  public ObservableAssert(Observable<T> completable) {
    super(completable, ObservableAssert.class);
    observer = TestObserverAssert.assertThat(completable.test());
  }

  static <T> ObservableAssert<T> assertThat(Observable<T> actual) {
    return new ObservableAssert<>(actual);
  }

  public ObservableAssert<T> isComplete() {
    observer.isComplete();
    return this;
  }

  public ObservableAssert<T> emittedNothing() {
    observer.observedNothing();
    return this;
  }
}
