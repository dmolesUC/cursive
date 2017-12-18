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
  private final Vertex root;

  // ------------------------------------------------------
  // Constructor

  public GraphStore() {
    // TODO: configure alternative graph DBs
    // TODO: in-memory TinkerGraph specifics like indices
    graph = TinkerGraph.open();
    root = graph.addVertex(Labels.STORE);
  }

  // ------------------------------------------------------
  // Public

  Vertex root() {
    return root;
  }

  // ------------------------------------------------------
  // Resource

  @Override
  public Traversable<Workspace> workspaces() {
    // workspaces can only be direct children
    return findWorkspaces(children());
  }

  @Override
  public GraphWorkspace createWorkspace() {
    Vertex v = VertexUtils.createChild(root, ResourceType.WORKSPACE);
    return new GraphWorkspace(v);
  }

  @Override
  public Traversable<PcdmCollection> collections() {
    return findCollections(descendants());
  }

  @Override
  public GraphCollection createCollection() {
    Vertex v = VertexUtils.createChild(root, ResourceType.COLLECTION);
    return new GraphCollection(v);
  }

  @Override
  public Traversable<PcdmObject> objects() {
    return findObjects(descendants());
  }

  @Override
  public GraphObject createObject() {
    Vertex v = VertexUtils.createChild(root, ResourceType.OBJECT);
    return new GraphObject(v);
  }

  @Override
  public Traversable<PcdmFile> files() {
    return findFiles(descendants());

  }

  @Override
  public Traversable<PcdmRelation> relations() {
    return null;
  }

  @Override
  public Option<Resource> find(String identifier) {
    // TODO: figure out how to (1) ensure string internal IDs or (2) map back from strings to internal IDs
    Stream<Vertex> vertices = Stream.ofAll(() -> graph.vertices(identifier));
    return vertices.headOption()
      .flatMap(VertexUtils::toResource);
  }

  // ------------------------------------------------------
  // Private methods

  private Stream<Vertex> children() {
    return childrenOf(root);
  }

  // TODO: benchmark this vs. adding type nodes & relating all vertices of type to those nodes
  private Stream<Vertex> descendants() {
    return descendantsOf(root);
  }
}
