package org.cdlib.cursive.util;

import io.reactivex.Maybe;
import org.assertj.core.api.AbstractAssert;

import java.util.function.Predicate;

public class MaybeAssert<T> extends AbstractAssert<MaybeAssert<T>, Maybe<T>> {

  private final TestObserverAssert<T> observer;

  public MaybeAssert(Maybe<T> completable) {
    super(completable, MaybeAssert.class);
    observer = TestObserverAssert.assertThat(completable.test());
  }

  static <T> MaybeAssert<T> assertThat(Maybe<T> actual) {
    return new MaybeAssert<>(actual);
  }

  public MaybeAssert<T> wasEmpty() {
    observer.observedNothing();
    return this;
  }

  public MaybeAssert<T> emitted(T t) {
    observer.observed(t);
    return this;
  }

  public MaybeAssert<T> emittedError(Throwable expected) {
    observer.observedError(expected);
    return this;
  }

  public MaybeAssert<T> emittedValueThat(Predicate<? super T> predicate) {
    observer.observed(predicate);
    return this;
  }
}
