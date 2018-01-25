package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

class MemoryCollection extends ResourceImpl implements PcdmCollection {

  // --------------------
  // Fields

  private MemoryStore store;

  private Option<Workspace> parentWorkspace;
  private Option<PcdmCollection> parentCollection;

  private final AtomicReference<Vector<PcdmCollection>> memberCollections = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<PcdmObject>> memberObjects = new AtomicReference<>(Vector.empty());

  // --------------------
  // Constructors

  MemoryCollection(MemoryStore store, UUID identifier) {
    this(store, identifier, null, null);
  }

  MemoryCollection(MemoryStore store, UUID identifier, MemoryWorkspace parentWorkspace) {
    this(store, identifier, parentWorkspace, null);
  }

  MemoryCollection(MemoryStore store, UUID identifier, MemoryCollection parentCollection) {
    this(store, identifier, null, parentCollection);
  }

  private MemoryCollection(MemoryStore store, UUID identifier, MemoryWorkspace parentWorkspace, MemoryCollection parentCollection) {
    super(identifier);
    Objects.requireNonNull(store, () -> String.format("%s must have a Store", getClass().getSimpleName()));
    if (parentWorkspace != null && parentCollection != null) {
      throw new IllegalArgumentException(String.format("Collection can have at most one parent: %s, %s", parentWorkspace, parentCollection));
    }

    this.store = store;
    this.parentWorkspace = Option.of(parentWorkspace);
    this.parentCollection = Option.of(parentCollection);
  }

  // --------------------
  // Parents

  @Override
  public Option<Workspace> parentWorkspace() {
    return parentWorkspace;
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return parentCollection;
  }

  // --------------------
  // Member objects

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return memberObjects.get();
  }

  @Override
  public PcdmObject createObject() {
    Lazy<PcdmObject> newObject = Lazy.of(() -> store.createObject(this));
    memberObjects.updateAndGet(v -> v.append(newObject.get()));
    return newObject.get();
  }

  // --------------------
  // Member collections

  @Override
  public Traversable<PcdmCollection> memberCollections() {
    return memberCollections.get();
  }

  @Override
  public PcdmCollection createCollection() {
    Lazy<PcdmCollection> newCollection = Lazy.of(() -> store.createCollection(this));
    memberCollections.updateAndGet(v -> v.append(newCollection.get()));
    return newCollection.get();
  }
}
