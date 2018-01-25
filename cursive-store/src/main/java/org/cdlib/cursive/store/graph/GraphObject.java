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

  GraphObject(GraphStore store, Vertex vertex) {
    super(ResourceType.OBJECT, store, vertex);
  }

  // ------------------------------------------------------
  // Parents

  @Override
  public Option<PcdmObject> parentObject() {
    return store().findFirstObject(parents());
  }

  @Override
  public Option<PcdmCollection> parentCollection() {
    return store().findFirstCollection(parents());
  }

  // ------------------------------------------------------
  // Objects

  @Override
  public Traversable<PcdmObject> memberObjects() {
    return store().memberObjects(vertex());
  }

  @Override
  public GraphObject createObject() {
    return store().createObject(vertex());
  }

  // ------------------------------------------------------
  // Files

  @Override
  public Traversable<PcdmFile> memberFiles() {
    return store().findFiles(children());
  }

  @Override
  public GraphFile createFile() {
    return store().createFile(vertex());
  }

  // ------------------------------------------------------
  // Relations

  @Override
  public Traversable<PcdmObject> relatedObjects() {
    Iterator<Edge> edges = vertex().edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(Edge::inVertex).map(v -> new GraphObject(store(), v));
  }

  @Override
  public PcdmRelation relateTo(PcdmObject toObject) {
    // TODO: error handling for bad casts
    Vertex fromVertex = vertex();
    Vertex toVertex = ((GraphObject) toObject).vertex();
    Edge edge = fromVertex.addEdge(Labels.RELATION, toVertex);
    return new GraphRelation(store(), edge);
  }

  @Override
  public Traversable<PcdmRelation> outgoingRelations() {
    Iterator<Edge> edges = vertex().edges(Direction.OUT, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(edge -> new GraphRelation(store(), edge));
  }

  @Override
  public Traversable<PcdmRelation> incomingRelations() {
    Iterator<Edge> edges = vertex().edges(Direction.IN, Labels.RELATION);
    return Stream.ofAll(() -> edges).map(edge -> new GraphRelation(store(), edge));
  }
}
