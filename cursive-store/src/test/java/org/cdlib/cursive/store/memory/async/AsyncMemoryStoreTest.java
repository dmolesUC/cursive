package org.cdlib.cursive.store.memory.async;

import org.cdlib.cursive.store.rx.AbstractAsyncStoreTest;

public class AsyncMemoryStoreTest extends AbstractAsyncStoreTest<AsyncMemoryStore> {
  @Override
  AsyncMemoryStore newStore() {
    return new AsyncMemoryStore();
  }
}
