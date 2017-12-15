package org.cdlib.cursive.store.graph;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmObject;

class GraphCollection extends AbstractGraphResource implements PcdmCollection {

  GraphCollection(Vertex vertex) {
    super(ResourceType.COLLECTION, vertex);
  }

  @Override
  public Option<Workspace> parentWorkspace() {
    return VertexUtils.findFirstWorkspace(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return VertexUtils.findFirstCollection(parents());
  }

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return VertexUtils.findObjects(children());
  }

  @Override
  public PcdmObject createObject() {
    Vertex t = VertexUtils.createChild(vertex, ResourceType.OBJECT);
    return new GraphObject(t);
  }

  @Override
  public Traversable<PcdmCollection> memberCollections() {
    return VertexUtils.findCollections(children());
  }

  @Override
  public PcdmCollection createCollection() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.COLLECTION);
    return new GraphCollection(v);
  }
}
