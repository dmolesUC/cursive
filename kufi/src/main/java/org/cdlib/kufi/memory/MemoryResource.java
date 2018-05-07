package org.cdlib.kufi.memory;

import io.vavr.collection.Array;
import org.cdlib.kufi.AbstractResource;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Version;

import java.util.UUID;

public class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

  final MemoryStore store;

  MemoryResource(ResourceType<R> type, UUID id, Version version, MemoryStore store) {
    super(type, id, version);
    this.store = store;
  }

  @Override
  public final String toString() {
    return Array.of(id(), version(), store).mkString(getClass().getSimpleName() + "(", ", ", ")");
  }
}
