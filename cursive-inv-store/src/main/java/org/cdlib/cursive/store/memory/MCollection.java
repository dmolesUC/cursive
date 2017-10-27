package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CWorkspace;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class MCollection extends ResourceImpl implements CCollection {

  // --------------------
  // Fields

  private MemoryStore store;

  private Option<CWorkspace> parentWorkspace;
  private Option<CCollection> parentCollection;

  private final AtomicReference<Vector<CCollection>> memberCollections = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CObject>> memberObjects = new AtomicReference<>(Vector.empty());

  // --------------------
  // Constructors

  MCollection(MemoryStore store, String identifier) {
    this(store, identifier, null, null);
  }

  MCollection(MemoryStore store, String identifier, MWorkspace parentWorkspace) {
    this(store, identifier, parentWorkspace, null);
  }

  MCollection(MemoryStore store, String identifier, MCollection parentCollection) {
    this(store, identifier, null, parentCollection);
  }

  private MCollection(MemoryStore store, String identifier, MWorkspace parentWorkspace, MCollection parentCollection) {
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
  public Option<CWorkspace> parentWorkspace() {
    return parentWorkspace;
  }

  @Override
  public Option<CCollection> parentCollection() {
    return parentCollection;
  }

  // --------------------
  // Member objects

  @Override
  public Traversable<CObject> memberObjects() {
    return memberObjects.get();
  }

  @Override
  public CObject createObject() {
    Lazy<CObject> newObject = Lazy.of(() -> store.createObject(this));
    memberObjects.updateAndGet(v -> v.append(newObject.get()));
    return newObject.get();
  }

  // --------------------
  // Member collections

  @Override
  public Traversable<CCollection> memberCollections() {
    return memberCollections.get();
  }

  @Override
  public CCollection createCollection() {
    Lazy<CCollection> newCollection = Lazy.of(() -> store.createCollection(this));
    memberCollections.updateAndGet(v -> v.append(newCollection.get()));
    return newCollection.get();
  }
}
