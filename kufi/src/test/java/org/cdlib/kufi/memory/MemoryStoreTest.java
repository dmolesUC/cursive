package org.cdlib.kufi.memory;

import org.cdlib.kufi.AbstractStoreTest;

public class MemoryStoreTest extends AbstractStoreTest<MemoryStore> {
  @Override
  protected MemoryStore newStore() {
    return new MemoryStore();
  }
}
