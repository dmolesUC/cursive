package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.commons.configuration.BaseConfiguration;
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
    return childrenOf(root, Labels.labelFor(ResourceType.WORKSPACE)).map(GraphWorkspace::new);
  }

  @Override
  public GraphWorkspace createWorkspace() {
    Vertex v = VertexUtils.createChild(root, ResourceType.WORKSPACE);
    return new GraphWorkspace(v);
  }

  @Override
  public Traversable<PcdmCollection> collections() {
    return descendantsOf(root, Labels.labelFor(ResourceType.COLLECTION)).map(GraphCollection::new);
  }

  @Override
  public GraphCollection createCollection() {
    Vertex v = VertexUtils.createChild(root, ResourceType.COLLECTION);
    return new GraphCollection(v);
  }

  @Override
  public Traversable<PcdmObject> objects() {
    return descendantsOf(root, Labels.labelFor(ResourceType.OBJECT)).map(GraphObject::new);
  }

  @Override
  public GraphObject createObject() {
    Vertex v = VertexUtils.createChild(root, ResourceType.OBJECT);
    return new GraphObject(v);
  }

  @Override
  public Traversable<PcdmFile> files() {
    return findFiles(descendantsOf(root));

  }

  @Override
  public Traversable<PcdmRelation> relations() {
    return null;
  }

  @Override
  public Option<Resource> find(String identifier) {
    try {
      // TODO: look into what ID types are supported by Neo4J, Janus, and Neptune
      Long longId = Long.valueOf(identifier);
      Stream<Vertex> vertices = Stream.ofAll(() -> graph.vertices(longId));
      return vertices.headOption()
        .flatMap(VertexUtils::toResource);
    } catch (NumberFormatException e) {
      // TODO: use Try (if we still need this after sorting out IDs)
      return Option.none();
    }
  }

  // ------------------------------------------------------
  // Private methods

  private Stream<Vertex> children() {
    return childrenOf(root);
  }

}
