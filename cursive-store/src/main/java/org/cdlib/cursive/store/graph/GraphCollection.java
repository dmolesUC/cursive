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
    return GraphResourceUtils.findFirstWorkspace(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return GraphResourceUtils.findFirstCollection(parents());
  }

  public Traversable<PcdmObject> memberObjects() {
    return GraphResourceUtils.memberObjects(this.vertex());
  }

  public GraphObject createObject() {
    return GraphResourceUtils.createObject(this.vertex());
  }

  @Override
  public Traversable<PcdmCollection> memberCollections() {
    return GraphResourceUtils.memberCollections(this.vertex());
  }

  @Override
  public GraphCollection createCollection() {
    return GraphResourceUtils.createCollection(this.vertex());
  }
}

