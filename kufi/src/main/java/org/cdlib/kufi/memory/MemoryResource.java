package org.cdlib.kufi.memory;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.kufi.AbstractResource;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Version;

import java.util.UUID;

abstract class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

  // ------------------------------------------------------------
  // Instance fields

  final MemoryStore store;

  // ------------------------------------------------------------
  // Constructors

  MemoryResource(ResourceType<R> type, UUID id, Version currentVersion, Option<Version> deletedAt, MemoryStore store) {
    super(type, id, currentVersion, deletedAt);
    this.store = store;
  }

  // ------------------------------------------------------------
  // Resource

  @Override
  public final String toString() {
    return Array.of(id(), currentVersion(), store).mkString(getClass().getSimpleName() + "(", ", ", ")");
  }

  // ------------------------------------------------------------
  // Package-private

  MemoryStore store() {
    return store;
  }
}
