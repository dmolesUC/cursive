package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.core.Store;

import java.util.concurrent.atomic.AtomicReference;

public class MemoryStore implements Store {

  // ------------------------------------------------------------
  // Data

  private final AtomicReference<Vector<CWorkspace>> workspaces = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CCollection>> collections = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CObject>> objects = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CFile>> files = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CRelation>> relations = new AtomicReference<>(Vector.empty());

  private final AtomicReference<Map<String, Identified>> identifiers = new AtomicReference<>(HashMap.empty());

  // ------------------------------------------------------------
  // Store

  private String mintIdentifier() {
    return Identifiers.mintIdentifier();
  }

  private <T extends Identified> void register(AtomicReference<Vector<T>> registry, Lazy<T> lazyValue) {
    registry.updateAndGet(v -> v.append(lazyValue.get()));
    T value = lazyValue.get();
    String identifier = value.identifier();
    identifiers.updateAndGet(m -> m.put(identifier, value));
  }

  // --------------------
  // Workspaces

  @Override
  public Traversable<CWorkspace> workspaces() {
    return workspaces.get();
  }

  @Override
  public CWorkspace createWorkspace() {
    Lazy<CWorkspace> newWorkspace = Lazy.of(() -> new MWorkspace(this, mintIdentifier()));
    register(workspaces, newWorkspace);
    return newWorkspace.get();
  }

  // --------------------
  // Collections

  @Override
  public Traversable<CCollection> collections() {
    return collections.get();
  }

  @Override
  public CCollection createCollection() {
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this, mintIdentifier()));
    register(collections, newCollection);
    return newCollection.get();
  }

  CCollection createCollection(MWorkspace parent) {
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this, mintIdentifier(), parent));
    register(collections, newCollection);
    return newCollection.get();
  }

  CCollection createCollection(MCollection parent) {
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this, mintIdentifier(), parent));
    register(collections, newCollection);
    return newCollection.get();
  }

  // --------------------
  // Objects

  @Override
  public Traversable<CObject> objects() {
    return objects.get();
  }

  @Override
  public CObject createObject() {
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this, mintIdentifier()));
    register(objects, newObject);
    return newObject.get();
  }

  CObject createObject(MObject parent) {
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this, mintIdentifier(), parent));
    register(objects, newObject);
    return newObject.get();
  }

  CObject createObject(MCollection parent) {
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this, mintIdentifier(), parent));
    register(objects, newObject);
    return newObject.get();
  }

  // --------------------
  // Files

  @Override
  public Traversable<CFile> files() {
    return files.get();
  }

  // TODO: create files in objects, replace this with recordFile() or similar
  CFile createFile(MObject parent) {
    Lazy<CFile> newFile = Lazy.of(() -> new MFile(parent, mintIdentifier()));
    register(files, newFile);
    return newFile.get();
  }

  // --------------------
  // Relationships

  @Override
  public Traversable<CRelation> relations() {
    return relations.get();
  }

  void recordRelation(MRelation relation) {
    relations.updateAndGet(v -> v.append(relation));
  }

}
