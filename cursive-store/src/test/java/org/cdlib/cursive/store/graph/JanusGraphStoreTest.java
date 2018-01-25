package org.cdlib.cursive.store.graph;

import org.cdlib.cursive.store.AbstractStoreTest;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.Disabled;

@Disabled("JanusGraphStoreTest disabled pending fix for https://github.com/JanusGraph/janusgraph/issues/867")
public class JanusGraphStoreTest extends AbstractStoreTest<GraphStore> {
  @Override
  GraphStore newStore() {
    JanusGraph graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
    return new GraphStore(graph);
  }
}
