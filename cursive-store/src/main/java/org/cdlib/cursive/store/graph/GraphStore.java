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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.cdlib.cursive.store.graph.Labels.PARENT_CHILD;
import static org.cdlib.cursive.store.graph.VertexUtils.childrenOf;
import static org.cdlib.cursive.store.graph.VertexUtils.descendantsOf;

public class GraphStore implements Store {

  // ------------------------------------------------------
  // Constants

  private static final Logger log = LoggerFactory.getLogger(GraphStore.class);

  // ------------------------------------------------------
  // Fields

  private final Graph graph;
  private final Vertex root;
  private final long rootId;

  // ------------------------------------------------------
  // Constructor

  public GraphStore(Graph graph) {
    this.graph = graph;
    root = graph.addVertex(Labels.STORE);
    rootId = (long) root.id();
  }

  // ------------------------------------------------------
  // Creator methods

  GraphFile createFile(Vertex parent) {
    Vertex v = createChild(parent, ResourceType.FILE);
    return new GraphFile(this, v);
  }

  GraphCollection createCollection(Vertex parent) {
    Vertex v = createChild(parent, ResourceType.COLLECTION);
    return new GraphCollection(this, v);
  }

  GraphObject createObject(Vertex parent) {
    Vertex v = createChild(parent, ResourceType.OBJECT);
    return new GraphObject(this, v);
  }

  private Vertex createChild(Vertex parent, ResourceType type) {
    Graph graph = parent.graph();
    Vertex child = graph.addVertex(Labels.labelFor(type));
    parent.addEdge(PARENT_CHILD, child);
    return child;
  }

  // ------------------------------------------------------
  // Finder methods

  Stream<PcdmObject> findObjects(Stream<Vertex> vertices) {
    return vertices.filter(GraphResourceUtils::isObject).map((Vertex v) -> new GraphObject(this, v));
  }

  Option<PcdmObject> findFirstObject(Stream<Vertex> vertices) {
    return vertices.find(GraphResourceUtils::isObject).map((Vertex v) -> new GraphObject(this, v));
  }

  Option<PcdmCollection> findFirstCollection(Stream<Vertex> vertices) {
    return vertices.find(GraphResourceUtils::isCollection).map((Vertex v) -> new GraphCollection(this, v));
  }

  Option<Workspace> findFirstWorkspace(Stream<Vertex> vertices) {
    return vertices.find(GraphResourceUtils::isWorkspace).map((Vertex v) -> new GraphWorkspace(this, v));
  }

  Stream<PcdmFile> findFiles(Stream<Vertex> vertices) {
    return vertices.filter(GraphResourceUtils::isFile).map((Vertex v) -> new GraphFile(this, v));
  }

  Traversable<PcdmCollection> memberCollections(Vertex parent) {
    return childrenOf(parent, Labels.labelFor(ResourceType.COLLECTION))
      .map((Vertex v) -> new GraphCollection(this, v));
  }

  Traversable<PcdmObject> memberObjects(Vertex parent) {
    return childrenOf(parent, Labels.labelFor(ResourceType.OBJECT))
      .map((Vertex v) -> new GraphObject(this, v));
  }

  // ------------------------------------------------------
  // UUIDs

  /**
   * Hack to convert long IDs to fake UUIDs.
   *
   * @param vertex The vertex
   * @return A "UUID" where the least significant bits are the ID of the root vertex, and the most
   * significant bits are the ID of the specified vertex.
   */
  UUID getId(Vertex vertex) {
    // TODO: use native UUID IDs if the graph implementation supports them
    long vertexId = (long) vertex.id();
    return new UUID(vertexId, rootId);
  }

  private long toVertexId(UUID uuid) {
    if (invalid(uuid)) {
      throw new IllegalArgumentException("UUID " + uuid + " does not appear to belong to this graph");
    }
    return uuid.getMostSignificantBits();
  }

  private boolean invalid(UUID uuid) {
    return uuid.getLeastSignificantBits() != rootId;
  }

  // ------------------------------------------------------
  // Workspaces

  @Override
  public Traversable<Workspace> workspaces() {
    // Workspaces can only exist at the root
    return childrenOf(root, Labels.labelFor(ResourceType.WORKSPACE)).map(v -> new GraphWorkspace(this, v));
  }

  @Override
  public GraphWorkspace createWorkspace() {
    Vertex v = createChild(root, ResourceType.WORKSPACE);
    return new GraphWorkspace(this, v);
  }

  // ------------------------------------------------------
  // Collections

  @Override
  public Traversable<PcdmCollection> allCollections() {
    return descendantsOf(root, Labels.labelFor(ResourceType.COLLECTION)).map(v -> new GraphCollection(this, v));
  }

  @Override
  public GraphCollection createCollection() {
    return createCollection(root);
  }

  // ------------------------------------------------------
  // Objects

  @Override
  public Traversable<PcdmObject> allObjects() {
    return descendantsOf(root, Labels.labelFor(ResourceType.OBJECT)).map(v -> new GraphObject(this, v));
  }

  @Override
  public GraphObject createObject() {
    return createObject(root);
  }

  // ------------------------------------------------------
  // Files

  @Override
  public Traversable<PcdmFile> allFiles() {
    return findFiles(descendantsOf(root));
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
  public Option<Resource> find(UUID id) {
    if (invalid(id)) {
      log.warn("ID {} does not appear to belong to this graph", id);
      return Option.none();
    }
    long vertexId = toVertexId(id);
    Stream<Vertex> vertices = Stream.ofAll(() -> graph.vertices(vertexId));
    return vertices.headOption()
      .flatMap(v -> GraphResourceUtils.toResource(this, v));
  }

}
