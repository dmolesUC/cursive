package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;

public class MFile implements CFile {

  // --------------------
  // Fields

  private final CObject parentObject;

  // --------------------
  // Constructors

  MFile(CObject parentObject) {
    assert parentObject != null: "File must have an Object";
    this.parentObject = parentObject;
  }

  // --------------------
  // Parent objects

  @Override
  public CObject parentObject() {
    return parentObject;
  }
}
