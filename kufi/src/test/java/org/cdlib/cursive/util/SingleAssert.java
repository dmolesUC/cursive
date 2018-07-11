package org.cdlib.cursive.util;

import io.reactivex.Single;
import org.assertj.core.api.AbstractAssert;

import java.util.function.Predicate;

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

  public SingleAssert<T> emittedError(Throwable expected) {
    observer.observedError(expected);
    return this;
  }

  public SingleAssert<T> emittedErrorOfType(Class<? extends Throwable> expected) {
    observer.observedErrorOfType(expected);
    return this;
  }

  public SingleAssert<T> emittedValueThat(Predicate<? super T> predicate) {
    observer.observed(predicate);
    return this;
  }
}
