package org.cdlib.kufi.memory;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vavr.control.Either;
import org.cdlib.kufi.Collection;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.Version;
import org.cdlib.kufi.Workspace;

import java.util.UUID;

import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

class MemoryCollection extends MemoryResource<Collection> implements Collection {

  // ------------------------------------------------------------
  // Constructor

  MemoryCollection(UUID id, Version version, MemoryStore store) {
    super(COLLECTION, id, version, store);
  }

  // ------------------------------------------------------------
  // Collection

  @Override
  public Single<Either<Workspace, Collection>> parent() {
    return store.findParentOf(this).map(MemoryCollection::toParent);
  }

  @Override
  public Observable<Collection> childCollections() {
    return store.findChildrenOfType(this, COLLECTION);
  }

  // ------------------------------------------------------------
  // Class methods

  private static Either<Workspace, Collection> toParent(Resource<?> r) {
    return r.as(COLLECTION).toEither(() -> WORKSPACE.cast(r));
  }

}
