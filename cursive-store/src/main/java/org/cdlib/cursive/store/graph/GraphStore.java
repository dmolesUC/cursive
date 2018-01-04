package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import static org.cdlib.cursive.store.graph.VertexUtils.childrenOf;
import static org.cdlib.cursive.store.graph.VertexUtils.descendantsOf;

public class GraphStore implements Store {

  // ------------------------------------------------------
  // Fields

  private final Graph graph;
  private final Vertex root;

  // ------------------------------------------------------
  // Constructor

  public GraphStore(Graph graph) {
    this.graph = graph;
    this.root = graph.addVertex(Labels.STORE);
  }

  // ------------------------------------------------------
  // Public

  Vertex root() {
    return root;
  }

  // ------------------------------------------------------
  // Workspaces

  @Override
  public Traversable<Workspace> workspaces() {
    // Workspaces can only exist at the root
    return childrenOf(root, Labels.labelFor(ResourceType.WORKSPACE)).map(GraphWorkspace::new);
  }

  @Override
  public GraphWorkspace createWorkspace() {
    Vertex v = GraphResourceUtils.createChild(root, ResourceType.WORKSPACE);
    return new GraphWorkspace(v);
  }

  // ------------------------------------------------------
  // Collections

  @Override
  public Traversable<PcdmCollection> allCollections() {
    return descendantsOf(root, Labels.labelFor(ResourceType.COLLECTION)).map(GraphCollection::new);
  }

  @Override
  public GraphCollection createCollection() {
    return GraphResourceUtils.createCollection(root);
  }

  // ------------------------------------------------------
  // Objects

  @Override
  public Traversable<PcdmObject> allObjects() {
    return descendantsOf(root, Labels.labelFor(ResourceType.OBJECT)).map(GraphObject::new);
  }

  @Override
  public GraphObject createObject() {
    return GraphResourceUtils.createObject(root);
  }

  // ------------------------------------------------------
  // Files

  @Override
  public Traversable<PcdmFile> allFiles() {
    return GraphResourceUtils.findFiles(descendantsOf(root));
  }

  // ------------------------------------------------------
  // Relations

  @Override
  public Traversable<PcdmRelation> allRelations() {
    // TODO: is there a faster graph-native way to do this?
    return allObjects().flatMap(PcdmObject::outgoingRelations);
  }

  // ------------------------------------------------------
  // Finders

  @Override
  public Option<Resource> find(String identifier) {
    try {
      // TODO: look into what ID types are supported by Neo4J, Janus, and Neptune
      Long longId = Long.valueOf(identifier);
      Stream<Vertex> vertices = Stream.ofAll(() -> graph.vertices(longId));
      return vertices.headOption()
        .flatMap(GraphResourceUtils::toResource);
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
