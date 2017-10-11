package org.cdlib.cursive.store.memory;

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

  @Override
  public Traversable<CWorkspace> workspaces() {
    return workspaces.get();
  }

  @Override
  public Traversable<CCollection> collections() {
    return collections.get();
  }

  @Override
  public Traversable<CObject> objects() {
    return objects.get();
  }

  @Override
  public Traversable<CFile> files() {
    return files.get();
  }
}
