package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CRelation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class MObject implements CObject {

  // --------------------
  // Fields

  private final MemoryStore store;

  private final Option<CCollection> parentCollection;
  private final Option<CObject> parentObject;

  private final AtomicReference<Vector<CObject>> memberObjects = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CFile>> memberFiles = new AtomicReference<>(Vector.empty());

  private final AtomicReference<Vector<CRelation>> incomingRelations = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CRelation>> outgoingRelations = new AtomicReference<>(Vector.empty());

  // --------------------
  // Constructors

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
    Objects.requireNonNull(store, "Object must have a Store");
    if (parentCollection != null && parentObject != null) {
      throw new IllegalArgumentException(String.format("Object can have at most one parent: %s, %s", parentCollection, parentObject));
    }

    this.store = store;
    this.parentCollection = Option.of(parentCollection);
    this.parentObject = Option.of(parentObject);
  }

  // --------------------
  // Parents

  @Override
  public Option<CObject> parentObject() {
    return parentObject;
  }

  @Override
  public Option<CCollection> parentCollection() {
    return parentCollection;
  }

  // --------------------
  // Member files

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
  // Relationships

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
    if (!(toObject instanceof MObject)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }
    MObject toObj = (MObject) toObject;
    if (!this.store.equals(toObj.store)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }

    Lazy<MRelation> newRelation = Lazy.of(() -> new MRelation(this, toObj));
    outgoingRelations.updateAndGet(v -> v.append(newRelation.get()));
    MRelation relation = newRelation.get();
    toObj.addIncomingRelation(relation);
    store.recordRelation(relation);
    return relation;
  }

  private void addIncomingRelation(MRelation relation) {
    if (!this.equals(relation.toObject())) {
      throw new IllegalArgumentException(String.format("Incoming relation points to wrong object: expected %s, was %s", this, relation.toObject()));
    }
    incomingRelations.updateAndGet(v -> v.append(relation));
  }

}
