package org.cdlib.kufi.memory;

import org.cdlib.kufi.Resource;

class CreateResult<R extends Resource<R>> {

  // ------------------------------------------------------------
  // Instance fields

  private final R resource;
  private final StoreState stateNext;

  // ------------------------------------------------------------
  // Factory method

  static <R extends Resource<R>> CreateResult<R> of(R resource, StoreState storeNext) {
    return new CreateResult<>(resource, storeNext);
  }

  // ------------------------------------------------------------
  // Constructor

  public CreateResult(R resource, StoreState stateNext) {
    this.resource = resource;
    this.stateNext = stateNext;
  }

  // ------------------------------------------------------------
  // Accessors

  R resource() {
    return resource;
  }

  StoreState stateNext() {
    return stateNext;
  }

}
