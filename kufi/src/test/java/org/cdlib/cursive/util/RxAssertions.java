package org.cdlib.cursive.util;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;

public class RxAssertions extends Assertions {

  public static <T> TestObserverAssert<T> assertThat(TestObserver<T> actual) {
    return TestObserverAssert.assertThat(actual);
  }

  public static CompletableAssert assertThat(Completable actual) {
    return CompletableAssert.assertThat(actual);
  }

  public static ObservableAssert assertThat(Observable actual) {
    return ObservableAssert.assertThat(actual);
  }

  public static SingleAssert assertThat(Single actual) {
    return SingleAssert.assertThat(actual);
  }

  public static MaybeAssert assertThat(Maybe actual) {
    return MaybeAssert.assertThat(actual);
  }

  public static boolean completed(Completable c) {
    assertThat(c).isNotNull();
    var observer = c.test();
    assertThat(observer.isTerminated()).isTrue();
    assertThat(observer).observedNoErrors();
    return true;
  }

  public static Throwable errorEmittedBy(Completable c) {
    assertThat(c).isNotNull();
    return errorObservedBy(c.test());
  }

  public static <T> T valueEmittedBy(Single<T> single) {
    assertThat(single).isNotNull();
    return valueObservedBy(single.test());
  }

  public static <T> T valueEmittedBy(Maybe<T> maybe) {
    assertThat(maybe).isNotNull();
    return valueObservedBy(maybe.test());
  }

  public static Throwable errorEmittedBy(Maybe<?> maybe) {
    assertThat(maybe).isNotNull();
    return errorObservedBy(maybe.test());
  }

  public static Throwable errorEmittedBy(Single<?> single) {
    assertThat(single).isNotNull();
    return errorObservedBy(single.test());
  }

  private static Throwable errorObservedBy(TestObserver<?> observer) {
    assertThat(observer).isNotNull();
    observer.awaitTerminalEvent();
    assertThat(observer.errorCount()).isEqualTo(1);
    var errors = List.ofAll(observer.errors());
    return errors.head();
  }

  private static <T> T valueObservedBy(TestObserver<T> observer) {
    assertThat(observer).isNotNull();
    observer.awaitTerminalEvent();
    assertThat(observer).observedNoErrors();
    assertThat(observer).hasValueCount(1);
    return firstValueObservedBy(observer);
  }

  public static <T> List<T> valuesEmittedBy(Observable<T> observable) {
    return valuesObservedBy(observable.test());
  }

  private static <T> List<T> valuesObservedBy(TestObserver<T> observer) {
    assertThat(observer).isNotNull();
    observer.awaitTerminalEvent();
    assertThat(observer).observedNoErrors();
    return List.ofAll(observer.values());
  }

  private static <T> T firstValueObservedBy(TestObserver<T> observer) {
    return valuesObservedBy(observer).head();
  }
}
