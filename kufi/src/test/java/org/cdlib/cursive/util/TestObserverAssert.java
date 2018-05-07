package org.cdlib.cursive.util;

import io.reactivex.observers.TestObserver;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import org.assertj.core.api.AbstractAssert;

import java.util.function.Predicate;

public class TestObserverAssert<T> extends AbstractAssert<TestObserverAssert<T>, TestObserver<T>> {

  // ------------------------------
  // Constructors

  private TestObserverAssert(TestObserver<T> obs) {
    super(obs, TestObserverAssert.class);
  }

  // ------------------------------
  // Factory methods

  static <T> TestObserverAssert<T> assertThat(TestObserver<T> actual) {
    return new TestObserverAssert<>(actual);
  }

  // ------------------------------
  // Assertions

  @SuppressWarnings("UnusedReturnValue")
  public TestObserverAssert<T> isComplete() {
    if (actual == null) {
      failWithMessage("Expected completed Observable, but found null instead");
    } else {
      var c = actual.completions();
      if (c == 0) {
        failWithMessage("Expected completed Observable, but was not completed");
      } else if (c > 1) {
        failWithMessage("Expected single completion, but found <%s> completions", c);
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public TestObserverAssert<T> observedNothing() {
    return observedNoValues()
      .observedNoErrors()
      ;
  }

  @SuppressWarnings("UnusedReturnValue")
  public TestObserverAssert<T> hasValueCount(int expectedCount) {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var actualCount = actual.valueCount();
      if (actualCount != expectedCount) {
        failWithMessage("Expected <%s> values, found <%s>", expectedCount, actualCount);
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public TestObserverAssert<T> observed(T expectedValue) {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var values = List.ofAll(actual.values());
      var valueCount = actual.valueCount();
      if (valueCount == 0) {
        failWithMessage("Expected <%s>, found nothing", expectedValue);
      } else {
        if (!values.contains(expectedValue)) {
          failWithMessage("Expected value <%s> not found; values: <%s>", expectedValue, format(values));
        }
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  @SafeVarargs
  public final TestObserverAssert<T> observed(T... expectedValues) {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var expected = Array.of(expectedValues);
      var valueCount = actual.valueCount();
      if (valueCount == 0) {
        failWithMessage("Expected <%s>, found nothing", format(expected));
      } else {
        var values = List.ofAll(actual.values());
        if (!values.containsAll(expected)) {
          failWithMessage("Expected values <%s> not found; values: <%s>", expected, format(values));
        }
      }
    }
    return this;
  }

  public TestObserverAssert<T> observed(Predicate<? super T> predicate) {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var values = List.ofAll(actual.values());
      var valueCount = actual.valueCount();
      if (valueCount == 0) {
        failWithMessage("Expected value, found nothing");
      } else {
        if (!values.find(predicate).isDefined()) {
          failWithMessage("Expected value matching predicate <%s>; found: <%s>", predicate, format(values));
        }
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  @SafeVarargs
  public final TestObserverAssert<T> observedExactly(T... expectedValues) {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var expected = Array.of(expectedValues);
      var valueCount = actual.valueCount();
      if (valueCount == 0) {
        failWithMessage("Expected <%s>, found nothing", format(expected));
      } else {
        var values = Array.ofAll(actual.values());
        if (!values.equals(expected)) {
          failWithMessage("Expected values <%s>, but found <%s>", expected, format(values));
        }
      }
    }
    return this;
  }

  public TestObserverAssert<T> observedOneError() {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var errorCount = actual.errorCount();
      if (errorCount != 1) {
        failWithMessage("Expected 1 error, found <%s>",
          format(
            List.ofAll(actual.errors())
              .map(this::formatException)
          )
        );
      }
    }
    return this;
  }

  public TestObserverAssert<T> observedNoErrors() {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var errorCount = actual.errorCount();
      if (errorCount != 0) {
        failWithMessage("Expected no errors, found <%s>",
          format(
            List.ofAll(actual.errors())
              .map(this::formatException)
          )
        );
      }
    }
    return this;
  }

  TestObserverAssert<T> observedNoValues() {
    if (actual == null) {
      failWithMessage("Expected TestObserver, but found null instead");
    } else {
      var valueCount = actual.valueCount();
      if (valueCount != 0) {
        failWithMessage("Expected no values, found <%s>",
          format(List.ofAll(actual.values()))
        );
      }
    }
    return this;
  }

  private <E> String format(Traversable<E> values) {
    return values.mkString("<", ", ", ">");
  }

  private String formatException(Throwable t) {
    return String.format("%s: %s", t.getClass().getSimpleName(), t.getMessage());
  }
}
