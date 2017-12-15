package org.cdlib.cursive.store.graph;

import org.cdlib.cursive.store.AbstractStoreTest;

public class GraphStoreTest extends AbstractStoreTest<GraphStore> {
  @Override
  protected GraphStore newStore() {
    return new GraphStore();
  }
}
