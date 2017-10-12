package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;

import java.util.concurrent.atomic.AtomicReference;

class MObject implements CObject {
  private final MemoryStore store;

  private final Option<CCollection> parentCollection;
  private final Option<CObject> parentObject;

  private final AtomicReference<Vector<CObject>> memberObjects = new AtomicReference<>(Vector.empty());

  MObject(MemoryStore store) {
    this(store, null, null);
  }

  MObject(MemoryStore store, MCollection parentCollection) {
    this(store, parentCollection, null);
  }

  MObject(MemoryStore store, MObject parentObject) {
    this(store, null, parentObject);
  }

  private MObject(MemoryStore store, MCollection parentCollection, MObject parentObject) {
    assert store != null : "Object must have a Store";
    assert parentCollection == null || parentObject == null : String.format("Collection can have at most one parent: %s, %s", parentCollection, parentObject);

    this.store = store;
    this.parentCollection = Option.of(parentCollection);
    this.parentObject = Option.of(parentObject);
  }

  @Override
  public Option<CObject> parentObject() {
    return parentObject;
  }

  @Override
  public Option<CCollection> parentCollection() {
    return parentCollection;
  }

  @Override
  public Traversable<CFile> memberFiles() {
    return null;
  }

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

  @Override
  public Traversable<CObject> relatedObjects() {
    return null;
  }
}
