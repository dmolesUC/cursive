package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CRelation;

import java.util.concurrent.atomic.AtomicReference;

class MObject implements CObject {
  private final MemoryStore store;

  private final Option<CCollection> parentCollection;
  private final Option<CObject> parentObject;

  private final AtomicReference<Vector<CObject>> memberObjects = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CFile>> memberFiles = new AtomicReference<>(Vector.empty());

  private final AtomicReference<Vector<CRelation>> incomingRelations = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CRelation>> outgoingRelations = new AtomicReference<>(Vector.empty());

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
    return memberFiles.get();
  }

  @Override
  public CFile createFile() {
    Lazy<CFile> newFile = Lazy.of(() -> store.createFile(this));
    memberFiles.updateAndGet(v -> v.append(newFile.get()));
    return newFile.get();
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
    return outgoingRelations.get().map(CRelation::toObject);
  }

  @Override
  public Traversable<CRelation> outgoingRelations() {
    return outgoingRelations.get();
  }

  @Override
  public Traversable<CRelation> incomingRelations() {
    return incomingRelations.get();
  }

  @Override
  public CRelation relateTo(CObject toObject) {
    Lazy<MRelation> newRelation = Lazy.of(() -> new MRelation(this, toObject));
    outgoingRelations.updateAndGet(v -> v.append(newRelation.get()));
    MRelation relation = newRelation.get();
    if (toObject instanceof MObject) {
      ((MObject) toObject).addIncomingRelation(relation);
    }
    store.recordRelation(relation);
    return relation;
  }

  private void addIncomingRelation(MRelation relation) {
    assert this.equals(relation.toObject()): String.format("Incoming relation points to wrong object: expected %s, was %s", this, relation.toObject());
    incomingRelations.updateAndGet(v -> v.append(relation));
  }

}
