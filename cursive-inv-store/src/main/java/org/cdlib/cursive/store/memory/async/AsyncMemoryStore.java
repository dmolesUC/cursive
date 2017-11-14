package org.cdlib.cursive.store.memory.async;

import org.cdlib.cursive.store.async.adapters.AsyncStoreAdapter;
import org.cdlib.cursive.store.memory.MemoryStore;

public class AsyncMemoryStore extends AsyncStoreAdapter<MemoryStore> {
  public AsyncMemoryStore() {
    super(new MemoryStore());
  }
}
