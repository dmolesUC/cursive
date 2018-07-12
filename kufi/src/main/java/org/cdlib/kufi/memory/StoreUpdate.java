package org.cdlib.kufi.memory;

import org.cdlib.kufi.Resource;

// TODO: can't we just get the new StoreState from the Resource somehow?
class StoreUpdate<R extends Resource<R>> {

  // ------------------------------------------------------------
  // Instance fields

  private final MemoryResource<R> resource;
  private final StoreState stateNext;

  // ------------------------------------------------------------
  // Factory method

  static <R extends Resource<R>> StoreUpdate<R> of(MemoryResource<R> resource, StoreState storeNext) {
    return new StoreUpdate<>(resource, storeNext);
  }

  // ------------------------------------------------------------
  // Constructor

  private StoreUpdate(MemoryResource<R> resource, StoreState stateNext) {
    this.resource = resource;
    this.stateNext = stateNext;
  }

  // ------------------------------------------------------------
  // Accessors

  MemoryResource<R> resource() {
    return resource;
  }

  StoreState stateNext() {
    return stateNext;
  }

}
