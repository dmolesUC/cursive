package org.cdlib.kufi.memory;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.kufi.*;

import java.util.UUID;

import static io.vavr.control.Option.some;

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
