package org.cdlib.cursive.util;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.vavr.Lazy;
import io.vavr.collection.List;
import org.assertj.core.api.AbstractAssert;

public class ObservableAssert<T> extends AbstractAssert<ObservableAssert<T>, Observable<T>> {

  // ------------------------------
  // Fields

  private final Lazy<TestObserver<T>> testObserver = Lazy.of(() -> {
    if (actual == null) {
      throw new IllegalStateException("Can't subscribe to null observable");
    }
    TestObserver<T> observer = new TestObserver<>();
    actual.subscribe(observer);
    return observer;
  });

  // ------------------------------
  // Constructors

  private ObservableAssert(Observable<T> obs) {
    super(obs, ObservableAssert.class);
  }

  // ------------------------------
  // Factory methods

  static <T> ObservableAssert<T> assertThat(Observable<T> actual) {
    return new ObservableAssert<>(actual);
  }

  // ------------------------------
  // Instance methods

  private TestObserver<T> testObserver() {
    return testObserver.get();
  }

  // ------------------------------
  // Assertions

  @SuppressWarnings("UnusedReturnValue")
  public ObservableAssert<T> isComplete() {
    if (actual == null) {
      failWithMessage("Expected completed Observable, but found null instead");
    } else {
      TestObserver<T> observer = testObserver();
      long c = observer.completions();
      if (c == 0) {
        failWithMessage("Expected completed Observable, but was not completed");
      } else if (c > 1) {
        failWithMessage("Expected single completion, but found <%s> completions", c);
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public ObservableAssert<T> isEmpty() {
    if (actual == null) {
      failWithMessage("Expected Observable, but found null instead");
    } else {
      TestObserver<T> observer = testObserver();
      int valueCount = observer.valueCount();
      if (valueCount != 0) {
        failWithMessage("Expected no values, found <%s>", valueCount);
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public ObservableAssert<T> hasNoErrors() {
    if (actual == null) {
      failWithMessage("Expected Observable, but found null instead");
    } else {
      TestObserver<T> observer = testObserver();
      int errorCount = observer.errorCount();
      if (errorCount != 0) {
        failWithMessage("Expected no errors, found <%s>",
          List.ofAll(observer.errors())
            .map(this::formatException)
            .mkString("<", ", ", ">")
        );
      }
    }
    return this;
  }

  private String formatException(Throwable t) {
    return String.format("%s: %s", t.getClass().getSimpleName(), t.getMessage());
  }
}
