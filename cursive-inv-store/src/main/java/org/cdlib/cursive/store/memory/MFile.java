package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;

public class MFile implements CFile {

  private final CObject parentObject;

  MFile(CObject parentObject) {
    assert parentObject != null: "File must have an Object";
    this.parentObject = parentObject;
  }

  @Override
  public CObject parentObject() {
    return parentObject;
  }
}
