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

class GraphObject extends AbstractGraphResource implements PcdmObject {

  // ------------------------------------------------------
  // Constructors

  GraphObject(Vertex vertex) {
    super(ResourceType.OBJECT, vertex);
  }

  // ------------------------------------------------------
  // Parents

  @Override
  public Option<PcdmObject> parentObject() {
    return GraphResourceUtils.findFirstObject(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return GraphResourceUtils.findFirstCollection(parents());
  }

  // ------------------------------------------------------
  // Objects

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return GraphResourceUtils.memberObjects(vertex());
  }

  @Override
  public GraphObject createObject() {
    return GraphResourceUtils.createObject(vertex());
  }

  // ------------------------------------------------------
  // Files

  @Override
  public Traversable<PcdmFile> memberFiles() {
    return GraphResourceUtils.findFiles(children());
  }

  @Override
  public GraphFile createFile() {
    Vertex v = GraphResourceUtils.createChild(vertex(), ResourceType.FILE);
    return new GraphFile(v);
  }

  // ------------------------------------------------------
  // Relations

  @Override
  public Traversable<PcdmObject> relatedObjects() {
    Iterator<Edge> edges = vertex().edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(Edge::inVertex).map(GraphObject::new);
  }

  @Override
  public PcdmRelation relateTo(PcdmObject toObject) {
    // TODO: error handling for bad casts
    Vertex fromVertex = vertex();
    Vertex toVertex = ((GraphObject) toObject).vertex();
    Edge edge = fromVertex.addEdge(Labels.RELATION, toVertex);
    return new GraphRelation(edge);
  }

  @Override
  public Traversable<PcdmRelation> outgoingRelations() {
    Iterator<Edge> edges = vertex().edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(GraphRelation::new);
  }

  @Override
  public Traversable<PcdmRelation> incomingRelations() {
    Iterator<Edge> edges = vertex().edges(Direction.IN, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(GraphRelation::new);
  }
}
