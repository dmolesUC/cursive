package org.cdlib.kufi.memory;

import io.vavr.collection.Array;
import org.cdlib.kufi.AbstractResource;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Version;

import java.util.UUID;

public abstract class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

  final MemoryStore store;

  MemoryResource(ResourceType<R> type, UUID id, Version currentVersion, MemoryStore store) {
    super(type, id, currentVersion);
    this.store = store;
  }

  MemoryResource(ResourceType<R> type, UUID id, Version currentVersion, Version deletedAt, MemoryStore store) {
    super(type, id, currentVersion, deletedAt);
    this.store = store;
  }

  @Override
  public final String toString() {
    return Array.of(id(), currentVersion(), store).mkString(getClass().getSimpleName() + "(", ", ", ")");
  }
}
