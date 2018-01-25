package org.cdlib.cursive.store.graph;

import io.vavr.collection.Traversable;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;

class GraphWorkspace extends AbstractGraphResource implements Workspace {

  GraphWorkspace(GraphStore store, Vertex vertex) {
    super(ResourceType.WORKSPACE, store, vertex);
  }

  public Traversable<PcdmCollection> memberCollections() {
    return store().memberCollections(vertex());
  }

  public GraphCollection createCollection() {
    return store().createCollection(vertex());
  }
}
