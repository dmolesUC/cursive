package org.cdlib.kufi.memory;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vavr.control.Either;
import org.cdlib.kufi.*;

import java.util.UUID;

import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

class MemoryCollection extends MemoryResource<Collection> implements Collection {

  // ------------------------------------------------------------
  // Constructor

  MemoryCollection(UUID id, Version currentVersion, MemoryStore store) {
    super(COLLECTION, id, currentVersion, store);
  }

  private MemoryCollection(UUID id, Version currentVersion, Version deletedAt, MemoryStore store) {
    super(COLLECTION, id, currentVersion, deletedAt, store);
  }

  // ------------------------------------------------------------
  // Resource

  @Override
  public Tombstone<Collection> toTombstone(Transaction tx) {
    var deletedAt = currentVersion().next(tx);
    var cNext = new MemoryCollection(id(), deletedAt, deletedAt, store);
    return new Tombstone<>(tx, cNext);
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
