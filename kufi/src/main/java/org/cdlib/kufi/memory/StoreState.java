package org.cdlib.kufi.memory;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.cdlib.kufi.Collection;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Workspace;

import java.security.SecureRandom;
import java.util.UUID;

class StoreState {

  // ------------------------------------------------------------
  // Class fields

  /**
   * Separate SecureRandom instance per thread to avoid contention
   */
  private static final ThreadLocal<NoArgGenerator> generator = ThreadLocal.withInitial(
    () -> Generators.randomBasedGenerator(new SecureRandom())
  );
  public static final long DEFAULT_VERSION = 0L;

  // ------------------------------------------------------------
  // Instance fields

  private long tx;
  private final Map<UUID, MemoryResource<?>> resources;
  private final Multimap<UUID, MemoryResource<?>> parentToChildren;
  private final Map<UUID, MemoryResource<?>> childToParent;

  // ------------------------------------------------------------
  // Constructors

  StoreState() {
    this(0L, HashMap.empty(), HashMultimap.withSet().empty(), HashMap.empty());
  }

  private StoreState(
    long txNext,
    Map<UUID, MemoryResource<?>> rsNext,
    Multimap<UUID, MemoryResource<?>> p2cNext,
    Map<UUID, MemoryResource<?>> c2pNext
  ) {
    tx = txNext;
    resources = rsNext;
    parentToChildren = p2cNext;
    childToParent = c2pNext;
  }

  // ------------------------------------------------------------
  // Package-private instance methods

  long transaction() {
    return tx;
  }

  // ------------------------------
  // Finders

  Option<MemoryResource<?>> find(UUID uuid) {
    return resources.get(uuid);
  }

  <R extends Resource<R>> Traversable<R> findChildrenOfType(UUID uuid, ResourceType<R> type) {
    return findChildren(uuid).filter(type::is).map(type.implType()::cast);
  }

  Option<MemoryResource<?>> findParent(UUID uuid) {
    return childToParent.get(uuid);
  }

  boolean hasChildren(UUID id) {
    return parentToChildren.containsKey(id);
  }

  // ------------------------------
  // Creators & Deletors

  Result<Workspace> createWorkspace(MemoryStore store) {
    var id = newId();
    var txNext = 1 + tx;
    var ws = new MemoryWorkspace(id, txNext, DEFAULT_VERSION, store);
    var rsNext = resources.put(id, ws);
    var storeNext = new StoreState(txNext, rsNext, parentToChildren, childToParent);
    return Result.of(ws, storeNext);
  }

  Result<Collection> createCollection(MemoryStore store, MemoryWorkspace parent) {
    var id = newId();
    var txNext = 1 + tx;
    var collection = new MemoryCollection(id, txNext, DEFAULT_VERSION, store);
    var rsNext = resources.put(id, collection);
    var p2cNext = parentToChildren.put(parent.id(), collection);
    var c2pNext = childToParent.put(id, parent);
    var storeNext = new StoreState(txNext, rsNext, p2cNext, c2pNext);
    return Result.of(collection, storeNext);
  }

  StoreState delete(MemoryResource<?> r) {
    var id = r.id();
    parentToChildren.get(id).forEach(children -> {
      throw new IllegalStateException("Can't delete " + r + "; " + children.length() + " children");
    });
    var txNext = 1 + tx;
    var rsNext = resources.remove(id);
    var p2cNext = childToParent.get(id).map(p -> parentToChildren.remove(p.id(), r)).getOrElse(parentToChildren);
    var c2pNext = childToParent.remove(id);
    return new StoreState(txNext, rsNext, p2cNext, c2pNext);
  }

  StoreState deleteRecursive(MemoryResource<?> r) {
    var txNext = 1 + tx;
    return deleteRecursive(r, txNext);
  }

  // ------------------------------------------------------------
  // Private instance methods

  private Traversable<MemoryResource<?>> findChildren(UUID uuid) {
    return parentToChildren.get(uuid).getOrElse(HashSet.empty());
  }

  /**
   * Recursively delete the specified resource and its children without incrementing
   * the transaction.
   *
   * @param r The resource to delete.
   * @param txNext The final transaction.
   * @return The final state.
   */
  private StoreState deleteRecursive(MemoryResource<?> r, long txNext) {
    var id = r.id();
    var rsNext = resources.remove(id);
    var p2cNext = childToParent.get(id).map(p -> parentToChildren.remove(p.id(), r)).getOrElse(parentToChildren);
    var c2pNext = childToParent.remove(id);
    var storeNext = new StoreState(txNext, rsNext, p2cNext, c2pNext);
    var children = p2cNext.get(id).getOrElse(HashSet.empty());
    return children.foldLeft(storeNext, (s, c) -> s.deleteRecursive(c, txNext));
  }

  private static UUID newId() {
    return generator.get().generate();
  }

  // ------------------------------------------------------------
  // Helper classes

  static class Result<T> {
    private T value;
    private StoreState state;

    private static <T> Result<T> of(T t, StoreState state) {
      return new Result<>(state, t);
    }

    private Result(StoreState state, T value) {
      this.value = value;
      this.state = state;
    }

    T value() {
      return value;
    }

    StoreState state() {
      return state;
    }
  }
}
