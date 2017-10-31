package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Objects;

class MemoryFile extends PcdmResourceImpl implements PcdmFile {

  // --------------------
  // Fields

  private final PcdmObject parentObject;

  // --------------------
  // Constructors

  MemoryFile(PcdmObject parentObject, String identifier) {
    super(identifier);
    Objects.requireNonNull(parentObject, () -> String.format("%s must have a parent", getClass().getSimpleName()));
    this.parentObject = parentObject;
  }

  // --------------------
  // Parent objects

  @Override
  public PcdmObject parentObject() {
    return parentObject;
  }
}
