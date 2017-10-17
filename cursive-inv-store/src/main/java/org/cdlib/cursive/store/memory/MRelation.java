package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CRelation;

public class MRelation implements CRelation {

  private final CObject fromObject;
  private final CObject toObject;

  MRelation(CObject fromObject, CObject toObject) {
    this.fromObject = fromObject;
    this.toObject = toObject;
  }

  @Override
  public CObject fromObject() {
    return fromObject;
  }

  @Override
  public CObject toObject() {
    return toObject;
  }
}
