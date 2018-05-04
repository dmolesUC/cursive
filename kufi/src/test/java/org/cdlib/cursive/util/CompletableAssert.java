package org.cdlib.cursive.util;

import io.reactivex.Completable;
import org.assertj.core.api.AbstractAssert;

public class CompletableAssert extends AbstractAssert<CompletableAssert, Completable> {

  private final TestObserverAssert<Void> observer;

  public CompletableAssert(Completable completable) {
    super(completable, CompletableAssert.class);
    observer = TestObserverAssert.assertThat(completable.test());
  }

  static CompletableAssert assertThat(Completable actual) {
    return new CompletableAssert(actual);
  }

  public CompletableAssert isComplete() {
    observer.isComplete();
    return this;
  }
}
