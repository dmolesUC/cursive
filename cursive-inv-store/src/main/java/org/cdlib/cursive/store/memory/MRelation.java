package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CRelation;

class MRelation implements CRelation {

  // --------------------
  // Fields

  private final CObject fromObject;
  private final CObject toObject;

  // --------------------
  // Constructors

  MRelation(CObject fromObject, CObject toObject) {
    this.fromObject = fromObject;
    this.toObject = toObject;
  }

  // --------------------
  // Related objects

  @Override
  public CObject fromObject() {
    return fromObject;
  }

  @Override
  public CObject toObject() {
    return toObject;
  }
}
