package org.cdlib.cursive.store.graph;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import static org.cdlib.cursive.store.graph.VertexUtils.*;

class GraphObject extends AbstractGraphResource implements PcdmObject {
  GraphObject(Vertex vertex) {
    super(ResourceType.OBJECT, vertex);
  }

  @Override
  public Option<PcdmObject> parentObject() {
    return findFirstObject(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return findFirstCollection(parents());
  }

  @Override
  public Traversable<PcdmFile> memberFiles() {
    return findFiles(children());
  }

  @Override
  public PcdmFile createFile() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.FILE);
    return new GraphFile(v);
  }

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return findObjects(children());
  }

  @Override
  public PcdmObject createObject() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.OBJECT);
    return new GraphObject(v);
  }

  @Override
  public Traversable<PcdmObject> relatedObjects() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public PcdmRelation relateTo(PcdmObject toObject) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Traversable<PcdmRelation> outgoingRelations() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Traversable<PcdmRelation> incomingRelations() {
    throw new UnsupportedOperationException("Not implemented");
  }
}
