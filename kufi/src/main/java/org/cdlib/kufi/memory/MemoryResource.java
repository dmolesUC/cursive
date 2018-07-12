package org.cdlib.kufi.memory;

import io.vavr.Lazy;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.cdlib.kufi.*;

import java.util.UUID;

abstract class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

  // ------------------------------------------------------------
  // Class fields

  private static final Lazy<Map<ResourceType<?>, ResourceConstructor<?>>> creators = Lazy.of(() -> HashMap.of(
    ResourceType.WORKSPACE, (ResourceConstructor<Workspace>) MemoryWorkspace::new,
    ResourceType.COLLECTION, (ResourceConstructor<Collection>) MemoryCollection::new
  ));

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

  @SuppressWarnings("unchecked")
  static <R extends Resource<R>> ResourceConstructor<R> creatorFor(ResourceType<R> type) {
    return creators.get().get(type).map(b -> (ResourceConstructor<R>) b).get();
  }
}
