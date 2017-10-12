package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CWorkspace;
import org.cdlib.cursive.store.Store;

import java.util.concurrent.atomic.AtomicReference;

public class MemoryStore implements Store {

  // ------------------------------------------------------------
  // Data

  private final AtomicReference<Vector<CWorkspace>> workspaces = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CCollection>> collections = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CObject>> objects = new AtomicReference<>(Vector.empty());
  private final AtomicReference<Vector<CFile>> files = new AtomicReference<>(Vector.empty());

  // ------------------------------------------------------------
  // Store

  // --------------------
  // Workspaces

  @Override
  public Traversable<CWorkspace> workspaces() {
    return workspaces.get();
  }

  @Override
  public CWorkspace createWorkspace() {
    Lazy<CWorkspace> newWorkspace = Lazy.of(() -> new MWorkspace(this));
    workspaces.updateAndGet(v -> v.append(newWorkspace.get()));
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
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this));
    collections.updateAndGet(v -> v.append(newCollection.get()));
    return newCollection.get();
  }

  CCollection createCollection(MWorkspace parent) {
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this, parent));
    collections.updateAndGet(v -> v.append(newCollection.get()));
    return newCollection.get();
  }

  CCollection createCollection(MCollection parent) {
    Lazy<CCollection> newCollection = Lazy.of(() -> new MCollection(this, parent));
    collections.updateAndGet(v -> v.append(newCollection.get()));
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
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this));
    objects.updateAndGet(v -> v.append(newObject.get()));
    return newObject.get();
  }

  CObject createObject(MObject parent) {
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this, parent));
    objects.updateAndGet(v -> v.append(newObject.get()));
    return newObject.get();
  }

  CObject createObject(MCollection parent) {
    Lazy<CObject> newObject = Lazy.of(() -> new MObject(this, parent));
    objects.updateAndGet(v -> v.append(newObject.get()));
    return newObject.get();
  }

  // --------------------
  // Files

  @Override
  public Traversable<CFile> files() {
    return files.get();
  }


  // ------------------------------------------------------------
  // Relationships


}
