package org.cdlib.kufi.memory;

import io.reactivex.Observable;
import org.cdlib.kufi.Collection;
import org.cdlib.kufi.Transaction;
import org.cdlib.kufi.Version;
import org.cdlib.kufi.Workspace;

import java.util.UUID;

import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;
import static org.cdlib.kufi.util.Preconditions.require;

class MemoryWorkspace extends MemoryResource<Workspace> implements Workspace {

  // ------------------------------------------------------------
  // Constructor

  MemoryWorkspace(UUID id, Version version, MemoryStore store) {
    super(WORKSPACE, id, version, store);
  }

  public MemoryWorkspace(UUID id, Version currentVersion, Version deletedAt, MemoryStore store) {
    super(WORKSPACE, id, currentVersion, deletedAt, store);
  }

  // ------------------------------------------------------------
  // Resource

  @Override
  public Workspace delete(Transaction tx) {  // TODO: find a way to pull this up
    var deletedAt = currentVersion().next(tx);
    return new MemoryWorkspace(id(), deletedAt, deletedAt, store);
  }

  @Override
  public Workspace nextVersion(Transaction tx) {  // TODO: find a way to pull this up
    require(isLive(), () -> "Can't create new version of deleted resource " + this);
    return new MemoryWorkspace(id(), currentVersion().next(tx), store);
  }

  // ------------------------------------------------------------
  // Workspace

  @Override
  public Observable<Collection> childCollections() {
    return store.findChildrenOfType(this, COLLECTION);
  }
}
