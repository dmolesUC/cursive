package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;

import java.util.Objects;

class MFile implements CFile {

  // --------------------
  // Fields

  private final CObject parentObject;

  // --------------------
  // Constructors

  MFile(CObject parentObject) {
    Objects.requireNonNull(parentObject, "Object must have a Store");
    this.parentObject = parentObject;
  }

  // --------------------
  // Parent objects

  @Override
  public CObject parentObject() {
    return parentObject;
  }
}
