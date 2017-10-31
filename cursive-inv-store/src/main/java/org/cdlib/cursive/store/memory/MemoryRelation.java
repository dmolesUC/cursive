package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

class MemoryRelation implements PcdmRelation {

  // --------------------
  // Fields

  private final PcdmObject fromObject;
  private final PcdmObject toObject;

  // --------------------
  // Constructors

  MemoryRelation(PcdmObject fromObject, PcdmObject toObject) {
    this.fromObject = fromObject;
    this.toObject = toObject;
  }

  // --------------------
  // Related objects

  @Override
  public PcdmObject fromObject() {
    return fromObject;
  }

  @Override
  public PcdmObject toObject() {
    return toObject;
  }
}
