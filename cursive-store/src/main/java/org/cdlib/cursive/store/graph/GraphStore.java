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

import java.util.UUID;

import static org.cdlib.cursive.store.graph.VertexUtils.childrenOf;
import static org.cdlib.cursive.store.graph.VertexUtils.descendantsOf;

public class GraphStore implements Store {

  // ------------------------------------------------------
  // Fields

  private final Graph graph;
  private final Vertex root;
  private final long rootId;

  // ------------------------------------------------------
  // Constructor

  public GraphStore(Graph graph) {
    this.graph = graph;
    this.root = graph.addVertex(Labels.STORE);
    this.rootId = (long) root.id();
  }

  // ------------------------------------------------------
  // Public

  Vertex root() {
    return root;
  }

  // TODO inline this
  GraphStore store() {
    return this;
  }

  /**
   * Hack to convert long IDs to fake UUIDs.
   * @param vertex The vertex
   * @return A "UUID" where the least significant bits are the ID of the root vertex, and the most
   *   significant bits are the ID of the specified vertex.
   */
  UUID getId(Vertex vertex) {
    // TODO: use native UUID IDs if the graph implementation supports them
    long vertexId = (long) vertex.id();
    return new UUID(vertexId, rootId);
  }

  private long toVertexId(UUID uuid) {
    if (uuid.getLeastSignificantBits() != rootId) {
      throw new IllegalArgumentException("UUID " + uuid + " does not appear to belong to this graph");
    }
    return uuid.getMostSignificantBits();
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
    Vertex v = GraphResourceUtils.createChild(root, ResourceType.WORKSPACE);
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
    return GraphResourceUtils.createCollection(this, root);
  }

  // ------------------------------------------------------
  // Objects

  @Override
  public Traversable<PcdmObject> allObjects() {
    return descendantsOf(root, Labels.labelFor(ResourceType.OBJECT)).map(v -> new GraphObject(this, v));
  }

  @Override
  public GraphObject createObject() {
    return GraphResourceUtils.createObject(this, root);
  }

  // ------------------------------------------------------
  // Files

  @Override
  public Traversable<PcdmFile> allFiles() {
    return GraphResourceUtils.findFiles(store(), descendantsOf(root));
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
    long vertexId = toVertexId(id);
    Stream<Vertex> vertices = Stream.ofAll(() -> graph.vertices(vertexId));
    return vertices.headOption()
      .flatMap(v -> GraphResourceUtils.toResource(this, v));
  }

  // ------------------------------------------------------
  // Private methods

  private Stream<Vertex> children() {
    return childrenOf(root);
  }

}
