package org.cdlib.kufi.memory;

import io.reactivex.Observable;
import io.vavr.control.Option;
import org.cdlib.kufi.Collection;
import org.cdlib.kufi.Version;
import org.cdlib.kufi.Workspace;

import java.util.UUID;

import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

class MemoryWorkspace extends MemoryResource<Workspace> implements Workspace {

  // ------------------------------------------------------------
  // Constructor

  MemoryWorkspace(UUID id, Version currentVersion, Option<Version> deletedAt, MemoryStore store) {
    super(WORKSPACE, id, currentVersion, deletedAt, store);
  }

  // ------------------------------------------------------------
  // Workspace

  @Override
  public Observable<Collection> childCollections() {
    return store.findChildrenOfType(this, COLLECTION);
  }
}
