package org.cdlib.cursive.store.graph;

import io.vavr.collection.Traversable;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;

class GraphWorkspace extends AbstractGraphResource implements Workspace {

  GraphWorkspace(Vertex vertex) {
    super(ResourceType.WORKSPACE, vertex);
  }

  @Override
  public Traversable<PcdmCollection> memberCollections() {
    return VertexUtils
      .childrenOf(vertex, Labels.labelFor(ResourceType.COLLECTION))
      .map(GraphCollection::new);
  }

  @Override
  public GraphCollection createCollection() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.COLLECTION);
    return new GraphCollection(v);
  }
}
