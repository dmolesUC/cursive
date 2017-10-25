package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;

import java.util.Objects;

class MFile extends IdentifiedImpl implements CFile {

  // --------------------
  // Fields

  private final CObject parentObject;

  // --------------------
  // Constructors

  MFile(CObject parentObject, String identifier) {
    super(identifier);
    Objects.requireNonNull(parentObject, () -> String.format("%s must have a parent", getClass().getSimpleName()));
    this.parentObject = parentObject;
  }

  // --------------------
  // Parent objects

  @Override
  public CObject parentObject() {
    return parentObject;
  }
}
