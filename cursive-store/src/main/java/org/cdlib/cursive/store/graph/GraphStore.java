package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import static org.cdlib.cursive.store.graph.VertexUtils.*;

public class GraphStore implements Store {

  // ------------------------------------------------------
  // Fields

  private final Graph graph;
  private final Vertex vertex;

  // ------------------------------------------------------
  // Constructor

  public GraphStore() {
    // TODO: configure alternative graph DBs
    // TODO: in-memory TinkerGraph specifics like indices
    graph = TinkerGraph.open();
    vertex = graph.addVertex(Labels.STORE);
  }

  // ------------------------------------------------------
  // Resource

  @Override
  public Traversable<Workspace> workspaces() {
    return findWorkspaces(children());
  }

  @Override
  public Workspace createWorkspace() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.WORKSPACE);
    return new GraphWorkspace(v);
  }

  @Override
  public Traversable<PcdmCollection> collections() {
    return findCollections(children());
  }

  @Override
  public PcdmCollection createCollection() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.COLLECTION);
    return new GraphCollection(v);
  }

  @Override
  public Traversable<PcdmObject> objects() {
    return findObjects(children());
  }

  @Override
  public PcdmObject createObject() {
    Vertex v = VertexUtils.createChild(vertex, ResourceType.OBJECT);
    return new GraphObject(v);
  }

  @Override
  public Traversable<PcdmFile> files() {
    return findFiles(children());

  }

  @Override
  public Traversable<PcdmRelation> relations() {
    return null;
  }

  @Override
  public Option<Resource> find(String identifier) {
    return null;
  }

  // ------------------------------------------------------
  // Private methods

  private Stream<Vertex> children() {
    return childrenOf(vertex);
  }
}
