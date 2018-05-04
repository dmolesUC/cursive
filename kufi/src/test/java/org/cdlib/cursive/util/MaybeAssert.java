package org.cdlib.cursive.util;

import io.reactivex.Maybe;
import org.assertj.core.api.AbstractAssert;

public class MaybeAssert<T> extends AbstractAssert<MaybeAssert<T>, Maybe<T>> {

  private final TestObserverAssert<T> observer;

  public MaybeAssert(Maybe<T> completable) {
    super(completable, MaybeAssert.class);
    observer = TestObserverAssert.assertThat(completable.test());
  }

  static <T> MaybeAssert<T> assertThat(Maybe<T> actual) {
    return new MaybeAssert<>(actual);
  }

  public MaybeAssert<T> isComplete() {
    observer.isComplete();
    return this;
  }

  public MaybeAssert<T> emittedNothing() {
    observer.observedNothing();
    return this;
  }
}
