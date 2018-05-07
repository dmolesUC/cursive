package org.cdlib.cursive.util;

import io.reactivex.Single;
import org.assertj.core.api.AbstractAssert;

public class SingleAssert<T> extends AbstractAssert<SingleAssert<T>, Single<T>> {

  private final TestObserverAssert<T> observer;

  public SingleAssert(Single<T> completable) {
    super(completable, SingleAssert.class);
    observer = TestObserverAssert.assertThat(completable.test());
  }

  static <T> SingleAssert<T> assertThat(Single<T> actual) {
    return new SingleAssert<>(actual);
  }

  public SingleAssert<T> emitted(T value) {
    observer.observed(value);
    return this;
  }

  public SingleAssert<T> emittedOneError() {
    observer.observedOneError();
    return this;
  }

}
