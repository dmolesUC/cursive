package org.cdlib.cursive.store.graph;

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.cdlib.cursive.store.AbstractStoreTest;

public class TinkerGraphStoreTest extends AbstractStoreTest<GraphStore> {
  @Override
  GraphStore newStore() {
    return new GraphStore(TinkerGraph.open());
  }
}
