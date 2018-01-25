package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

class MemoryWorkspace extends ResourceImpl implements Workspace {

  // --------------------
  // Fields

  private final MemoryStore store;
  private final AtomicReference<Vector<PcdmCollection>> memberCollections = new AtomicReference<>(Vector.empty());


  // --------------------
  // Constructors

  MemoryWorkspace(MemoryStore store, UUID identifier) {
    super(identifier);
    Objects.requireNonNull(store, () -> String.format("%s must have a Store", getClass().getSimpleName()));
    this.store = store;
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
