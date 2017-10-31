package org.cdlib.cursive.store.async.adapters;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.pcdm.PcdmResource;
import org.cdlib.cursive.pcdm.async.AsyncPcdmResource;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Objects;
import java.util.function.Function;

abstract class AsyncPcdmResourceImpl<R extends PcdmResource> implements AsyncPcdmResource {

  // ------------------------------
  // Fields

  final R delegate;

  // ------------------------------
  // Constructor

  AsyncPcdmResourceImpl(R delegate) {
    Objects.requireNonNull(delegate, () -> String.format("%s must have a delegate", getClass().getSimpleName()));
    this.delegate = delegate;
  }

  // ------------------------------
  // Factory methods

  private static final List<Tuple2<Class<? extends PcdmResource>, Function<PcdmResource, AsyncPcdmResourceImpl<?>>>> adapters = List.of(
    Tuple.of(Workspace.class, Adapters::toCWorkspace),
    Tuple.of(PcdmCollection.class, Adapters::toCCollection),
    Tuple.of(PcdmFile.class, Adapters::toCFile),
    Tuple.of(PcdmObject.class, Adapters::toCObject)
  );

  static AsyncPcdmResourceImpl<?> from(PcdmResource r) {
    Option<Function<PcdmResource, AsyncPcdmResourceImpl<?>>> adapter = adapters
      .find((t) -> t._1.isInstance(r))
      .map(Tuple2::_2);

    return adapter
      .map((a) -> a.apply(r))
      .getOrElseThrow(() -> Adapters.unknownResourceType(r));
  }

  // ------------------------------
  // AsyncPcdmResource

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

    AsyncPcdmResourceImpl<?> that = (AsyncPcdmResourceImpl<?>) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  // ------------------------------
  // Helper classes

  private static class Adapters {

    private static AsyncWorkspaceAdapter toCWorkspace(PcdmResource r) {
      return new AsyncWorkspaceAdapter((Workspace) r);
    }

    private static AsyncPcdmCollectionAdapter toCCollection(PcdmResource r) {
      return new AsyncPcdmCollectionAdapter((PcdmCollection) r);
    }

    private static AsyncPcdmFileAdapter toCFile(PcdmResource r) {
      return new AsyncPcdmFileAdapter((PcdmFile) r);
    }

    private static AsyncPcdmObjectAdapter toCObject(PcdmResource r) {
      return new AsyncPcdmObjectAdapter((PcdmObject) r);
    }

    private static IllegalArgumentException unknownResourceType(PcdmResource r) {
      return new IllegalArgumentException(String.format("Unknown resource type %s for resource <%s>", r.getClass().getName(), r));
    }

  }
}
