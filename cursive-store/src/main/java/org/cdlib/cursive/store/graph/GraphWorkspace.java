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

  public Traversable<PcdmCollection> memberCollections() {
    return GraphResourceUtils.memberCollections(this.vertex());
  }

  public GraphCollection createCollection() {
    return GraphResourceUtils.createCollection(this.vertex());
  }
}
