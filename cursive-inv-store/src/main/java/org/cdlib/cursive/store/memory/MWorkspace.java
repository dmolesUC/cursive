package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CWorkspace;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class MWorkspace extends ResourceImpl implements CWorkspace {

  // --------------------
  // Fields

  private final MemoryStore store;
  private final AtomicReference<Vector<CCollection>> memberCollections = new AtomicReference<>(Vector.empty());


  // --------------------
  // Constructors

  MWorkspace(MemoryStore store, String identifier) {
    super(identifier);
    Objects.requireNonNull(store, () -> String.format("%s must have a Store", getClass().getSimpleName()));
    this.store = store;
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
