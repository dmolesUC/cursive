package org.cdlib.cursive.store.memory;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CWorkspace;

class MCollection implements CCollection {

  private Option<CWorkspace> parentWorkspace;
  private Option<CCollection> parentCollection;
  private MemoryStore store;

  MCollection(MemoryStore store) {
    this(store, null, null);
  }

  MCollection(MemoryStore store, MWorkspace parentWorkspace) {
    this(store, parentWorkspace, null);
  }

  MCollection(MemoryStore store, MWorkspace parentWorkspace, MCollection parentCollection) {
    this.store = store;
    this.parentWorkspace = Option.of(parentWorkspace);
    this.parentCollection = Option.of(parentCollection);
  }

  @Override
  public Option<CWorkspace> parentWorkspace() {
    return parentWorkspace;
  }

  @Override
  public Option<CCollection> parentCollection() {
    return parentCollection;
  }

  @Override
  public Traversable<CObject> memberObjects() {
    return null;
  }

  @Override
  public Traversable<CCollection> memberCollections() {
    return null;
  }
}
