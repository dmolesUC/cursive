package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.store.AbstractStoreTest;

public class MemoryStoreTest extends AbstractStoreTest<MemoryStore> {
  @Override
  @SuppressWarnings("WeakerAccess")
  protected MemoryStore newStore() {
    return new MemoryStore();
  }
}
