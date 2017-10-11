package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.store.AbstractStoreTest;

public class MemoryStoreTest extends AbstractStoreTest<MemoryStore> {
  @Override
  protected MemoryStore newStore() {
    return new MemoryStore();
  }
}
