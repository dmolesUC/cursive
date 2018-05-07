package org.cdlib.kufi.memory;

import io.reactivex.Observable;
import org.cdlib.kufi.*;

import java.util.UUID;

import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

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
  public Tombstone<Workspace> toTombstone(Transaction tx) {
    var deletedAt = currentVersion().next(tx);
    var cNext = new MemoryWorkspace(id(), deletedAt, deletedAt, store);
    return new Tombstone<>(tx, cNext);
  }

  // ------------------------------------------------------------
  // Workspace

  @Override
  public Observable<Collection> childCollections() {
    return store.findChildrenOfType(this, COLLECTION);
  }
}
