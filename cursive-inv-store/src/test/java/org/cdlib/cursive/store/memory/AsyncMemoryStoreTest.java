package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.store.rx.AbstractAsyncStoreTest;
import org.cdlib.cursive.store.async.adapters.AsyncStoreAdapter;

public class AsyncMemoryStoreTest extends AbstractAsyncStoreTest<AsyncStoreAdapter<MemoryStore>> {
  @Override
  protected AsyncStoreAdapter<MemoryStore> newStore() {
    return new AsyncStoreAdapter<>(new MemoryStore());
  }
}
