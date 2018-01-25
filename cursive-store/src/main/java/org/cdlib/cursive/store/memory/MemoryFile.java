package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Objects;
import java.util.UUID;

class MemoryFile extends ResourceImpl implements PcdmFile {

  // --------------------
  // Fields

  private final PcdmObject parentObject;

  // --------------------
  // Constructors

  MemoryFile(PcdmObject parentObject, UUID identifier) {
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
