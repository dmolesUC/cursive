package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.store.rx.AbstractRxStoreTest;
import org.cdlib.cursive.store.rx.adapters.RxStoreAdapter;

public class MemoryStoreRxTest extends AbstractRxStoreTest<RxStoreAdapter<MemoryStore>> {
  @Override
  protected RxStoreAdapter<MemoryStore> newStore() {
    return new RxStoreAdapter<>(new MemoryStore());
  }
}
