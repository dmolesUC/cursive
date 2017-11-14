package org.cdlib.cursive.store.memory.async;

import org.cdlib.cursive.store.memory.async.AsyncMemoryStore;
import org.cdlib.cursive.store.rx.AbstractAsyncStoreTest;
import org.cdlib.cursive.store.async.adapters.AsyncStoreAdapter;

public class AsyncMemoryStoreTest extends AbstractAsyncStoreTest<AsyncMemoryStore> {
  @Override
  protected AsyncMemoryStore newStore() {
    return new AsyncMemoryStore();
  }
}
