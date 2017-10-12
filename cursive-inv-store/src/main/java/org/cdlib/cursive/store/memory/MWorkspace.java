package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CWorkspace;

import java.util.concurrent.atomic.AtomicReference;

class MWorkspace implements CWorkspace {

  private final MemoryStore store;

  private final AtomicReference<Vector<CCollection>> collections = new AtomicReference<>(Vector.empty());

  MWorkspace(MemoryStore store) {
    this.store = store;
  }

  @Override
  public Traversable<CCollection> memberCollections() {
    return collections.get();
  }

  @Override
  public CCollection createCollection() {
    Lazy<CCollection> newCollection = Lazy.of(() -> store.createCollection(this));
    collections.updateAndGet(v -> v.append(newCollection.get()));
    return newCollection.get();
  }
}
