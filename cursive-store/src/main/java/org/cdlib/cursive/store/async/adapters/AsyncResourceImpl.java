package org.cdlib.cursive.store.async.adapters;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.core.async.AsyncResource;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Objects;
import java.util.function.Function;

abstract class AsyncResourceImpl<R extends Resource> implements AsyncResource {

  // ------------------------------
  // Fields

  final R delegate;

  // ------------------------------
  // Constructor

  AsyncResourceImpl(R delegate) {
    Objects.requireNonNull(delegate, () -> String.format("%s must have a delegate", getClass().getSimpleName()));
    this.delegate = delegate;
  }

  // ------------------------------
  // Factory methods

  private static final Map<ResourceType, Function<Resource, AsyncResource>> adapters = HashMap.of(
    ResourceType.WORKSPACE, (Resource r) -> new AsyncWorkspaceAdapter((Workspace) r),
    ResourceType.COLLECTION, (Resource r) -> new AsyncPcdmCollectionAdapter((PcdmCollection) r),
    ResourceType.OBJECT, (Resource r) -> new AsyncPcdmObjectAdapter((PcdmObject) r),
    ResourceType.FILE, (Resource r) -> new AsyncPcdmFileAdapter((PcdmFile) r)
  );

  static Try<AsyncResource> from(Resource r) {
    return Try.of(() ->
      adapters
        .get(r.type())
        .getOrElseThrow(() -> unknownResourceType(r))
        .apply(r)
    );
  }

  private static IllegalArgumentException unknownResourceType(Resource r1) {
    return new IllegalArgumentException(String.format("Unknown resource type %s for resource <%s>", r1.getClass().getName(), r1));
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

    AsyncResourceImpl<?> that = (AsyncResourceImpl<?>) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  // ------------------------------
  // Helper classes

  private static class Adapters {

    private static AsyncWorkspaceAdapter toCWorkspace(Resource r) {
      return new AsyncWorkspaceAdapter((Workspace) r);
    }

    private static AsyncPcdmCollectionAdapter toCCollection(Resource r) {
      return new AsyncPcdmCollectionAdapter((PcdmCollection) r);
    }

    private static AsyncPcdmFileAdapter toCFile(Resource r) {
      return new AsyncPcdmFileAdapter((PcdmFile) r);
    }

    private static AsyncPcdmObjectAdapter toCObject(Resource r) {
      return new AsyncPcdmObjectAdapter((PcdmObject) r);
    }

  }
}
