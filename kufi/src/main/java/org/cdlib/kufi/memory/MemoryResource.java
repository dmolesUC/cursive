package org.cdlib.kufi.memory;

import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.cdlib.kufi.*;

import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.cdlib.kufi.util.Preconditions.require;

public abstract class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

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
  // Class accessors

  // TODO: move to MemoryStore
  static <R extends Resource<R>> R createNew(ResourceType<R> type, UUID id, Transaction createdAtTx, MemoryStore store) {
    return creatorFor(type).construct(id, Version.initVersion(createdAtTx), none(), store);
  }

  // ------------------------------------------------------------
  // Resource

  // TODO: move to MemoryStore
  public static <R extends Resource<R>> R nextVersion(Resource<R> resource, MemoryStore store, Transaction tx) {
    require(resource.isLive(), () -> "Can't create new version of deleted resource " + resource);
    return creatorFor(resource.type()).construct(resource.id(), resource.currentVersion().next(tx), none(), store);
  }

  public static <R extends Resource<R>> R delete(Resource<R> resource, MemoryStore store, Transaction tx) {
    if (resource.isLive()) {
      var type = resource.type();
      var nextVersion = resource.currentVersion().next(tx);
      return creatorFor(type).construct(resource.id(), nextVersion, some(nextVersion), store);
    }
    return resource.self();
  }

  @Override
  public final String toString() {
    return Array.of(id(), currentVersion(), store).mkString(getClass().getSimpleName() + "(", ", ", ")");
  }

  // ------------------------------------------------------------
  // Private class methods

  private static <R extends Resource<R>> ResourceConstructor<R> creatorFor(ResourceType<R> type) {
    return Constructors.creatorFor(type).get();
    // TODO: restore this once we figure out how to test it and/or exclude it from coverage; till then let Vavr throw NoSuchElementException
//    return Constructors.creatorFor(type).getOrElseThrow(() -> new IllegalArgumentException("Unknown resource type: " + type));
  }

  // ------------------------------------------------------------
  // Helper classes

  private static class Constructors {
    private static final Map<ResourceType<?>, ResourceConstructor<?>> creators = HashMap.of(
      ResourceType.WORKSPACE, (ResourceConstructor<Workspace>) MemoryWorkspace::new,
      ResourceType.COLLECTION, (ResourceConstructor<Collection>) MemoryCollection::new
    );

    @SuppressWarnings("unchecked")
    private static <R extends Resource<R>> Option<ResourceConstructor<R>> creatorFor(ResourceType<R> type) {
      return creators.get(type).map(b -> (ResourceConstructor<R>) b);
    }

  }
}
