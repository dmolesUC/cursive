package org.cdlib.cursive.store.graph;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmObject;

class GraphCollection extends AbstractGraphResource implements PcdmCollection {

  GraphCollection(GraphStore store, Vertex vertex) {
    super(ResourceType.COLLECTION, store, vertex);
  }

  @Override
  public Option<Workspace> parentWorkspace() {
    return store().findFirstWorkspace(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return store().findFirstCollection(parents());
  }

  public Traversable<PcdmObject> memberObjects() {
    return store().memberObjects(vertex());
  }

  public GraphObject createObject() {
    return store().createObject(vertex());
  }

  @Override
  public Traversable<PcdmCollection> memberCollections() {
    return store().memberCollections(vertex());
  }

  @Override
  public GraphCollection createCollection() {
    return store().createCollection(vertex());
  }
}

