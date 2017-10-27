package org.cdlib.cursive.store.rx.adapters;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.core.rx.RxResource;

import java.util.Objects;
import java.util.function.Function;

abstract class RxResourceImpl<R extends Resource> implements RxResource {

  // ------------------------------
  // Fields

  final R delegate;

  // ------------------------------
  // Constructor

  RxResourceImpl(R delegate) {
    Objects.requireNonNull(delegate, () -> String.format("%s must have a delegate", getClass().getSimpleName()));
    this.delegate = delegate;
  }

  // ------------------------------
  // Factory methods

  private static final List<Tuple2<Class<? extends Resource>, Function<Resource, RxResourceImpl<?>>>> adapters = List.of(
    Tuple.of(CWorkspace.class, Adapters::toCWorkspace),
    Tuple.of(CCollection.class, Adapters::toCCollection),
    Tuple.of(CFile.class, Adapters::toCFile),
    Tuple.of(CObject.class, Adapters::toCObject)
  );

  static RxResourceImpl<?> from(Resource r) {
    Option<Function<Resource, RxResourceImpl<?>>> adapter = adapters
      .find((t) -> t._1.isInstance(r))
      .map(Tuple2::_2);

    return adapter
      .map((a) -> a.apply(r))
      .getOrElseThrow(() -> Adapters.unknownResourceType(r));
  }

  // ------------------------------
  // RxResource

  @Override
  public String identifier() {
    return delegate.identifier();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RxResourceImpl<?> that = (RxResourceImpl<?>) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  // ------------------------------
  // Helper classes

  private static class Adapters {

    private static RxCWorkspaceAdapter toCWorkspace(Resource r) {
      return new RxCWorkspaceAdapter((CWorkspace) r);
    }

    private static RxCCollectionAdapter toCCollection(Resource r) {
      return new RxCCollectionAdapter((CCollection) r);
    }

    private static RxCFileAdapter toCFile(Resource r) {
      return new RxCFileAdapter((CFile) r);
    }

    private static RxCObjectAdapter toCObject(Resource r) {
      return new RxCObjectAdapter((CObject) r);
    }

    private static IllegalArgumentException unknownResourceType(Resource r) {
      return new IllegalArgumentException(String.format("Unknown resource type %s for resource <%s>", r.getClass().getName(), r));
    }

  }
}
