package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class MemoryObject extends PcdmResourceImpl implements PcdmObject {

  // --------------------
  // Fields

  private final MemoryStore store;

  private final Option<PcdmCollection> parentCollection;
  private final Option<PcdmObject> parentObject;

  private final AtomicReference<Vector<PcdmObject>> memberObjects = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<PcdmFile>> memberFiles = new AtomicReference<>(Vector.empty());

  private final AtomicReference<Vector<PcdmRelation>> incomingRelations = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<PcdmRelation>> outgoingRelations = new AtomicReference<>(Vector.empty());

  // --------------------
  // Constructors

  MemoryObject(MemoryStore store, String identifier) {
    this(store, identifier, null, null);
  }

  MemoryObject(MemoryStore store, String identifier, MemoryCollection parentCollection) {
    this(store, identifier, parentCollection, null);
  }

  MemoryObject(MemoryStore store, String identifier, MemoryObject parentObject) {
    this(store, identifier, null, parentObject);
  }

  private MemoryObject(MemoryStore store, String identifier, MemoryCollection parentCollection, MemoryObject parentObject) {
    super(identifier);
    Objects.requireNonNull(store, () -> String.format("%s must have a Store", getClass().getSimpleName()));
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
  public Option<PcdmObject> parentObject() {
    return parentObject;
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return parentCollection;
  }

  // --------------------
  // Member files

  @Override
  public Traversable<PcdmFile> memberFiles() {
    return memberFiles.get();
  }

  @Override
  public PcdmFile createFile() {
    Lazy<PcdmFile> newFile = Lazy.of(() -> store.createFile(this));
    memberFiles.updateAndGet(v -> v.append(newFile.get()));
    return newFile.get();
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
  // Relationships

  @Override
  public Traversable<PcdmObject> relatedObjects() {
    return outgoingRelations.get().map(PcdmRelation::toObject);
  }

  @Override
  public Traversable<PcdmRelation> outgoingRelations() {
    return outgoingRelations.get();
  }

  @Override
  public Traversable<PcdmRelation> incomingRelations() {
    return incomingRelations.get();
  }

  @Override
  public PcdmRelation relateTo(PcdmObject toObject) {
    if (!(toObject instanceof MemoryObject)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }
    MemoryObject toObj = (MemoryObject) toObject;
    if (!this.store.equals(toObj.store)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }

    Lazy<MemoryRelation> newRelation = Lazy.of(() -> new MemoryRelation(this, toObj));
    outgoingRelations.updateAndGet(v -> v.append(newRelation.get()));
    MemoryRelation relation = newRelation.get();
    toObj.addIncomingRelation(relation);
    store.recordRelation(relation);
    return relation;
  }

  private void addIncomingRelation(MemoryRelation relation) {
    if (!this.equals(relation.toObject())) {
      throw new IllegalArgumentException(String.format("Incoming relation points to wrong object: expected %s, was %s", this, relation.toObject()));
    }
    incomingRelations.updateAndGet(v -> v.append(relation));
  }

}
