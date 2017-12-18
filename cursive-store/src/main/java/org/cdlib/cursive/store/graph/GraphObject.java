package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import java.util.Iterator;

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
  public GraphFile createFile() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.FILE);
    return new GraphFile(v);
  }

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return findObjects(children());
  }

  @Override
  public GraphObject createObject() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.OBJECT);
    return new GraphObject(v);
  }

  @Override
  public Traversable<PcdmObject> relatedObjects() {
    Iterator<Edge> edges = vertex.edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(Edge::inVertex).map(GraphObject::new);
  }

  @Override
  public PcdmRelation relateTo(PcdmObject toObject) {
    // TODO: error handling for bad casts
    Vertex fromVertex = vertex;
    Vertex toVertex = ((GraphObject) toObject).vertex();
    Edge edge = fromVertex.addEdge(Labels.RELATION, toVertex);
    return new GraphRelation(edge);
  }

  @Override
  public Traversable<PcdmRelation> outgoingRelations() {
    Iterator<Edge> edges = vertex.edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(GraphRelation::new);
  }

  @Override
  public Traversable<PcdmRelation> incomingRelations() {
    Iterator<Edge> edges = vertex.edges(Direction.IN, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(GraphRelation::new);
  }
}
