package org.cdlib.cursive.store.graph;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.function.BiFunction;

import static org.cdlib.cursive.store.graph.Labels.PARENT_CHILD;

class GraphResourceUtils {

  // ------------------------------------------------------
  // Class fields

  private static final Map<ResourceType, BiFunction<GraphStore, Vertex, AbstractGraphResource>> adapters = HashMap.of(
    ResourceType.WORKSPACE, GraphWorkspace::new,
    ResourceType.COLLECTION, GraphCollection::new,
    ResourceType.OBJECT, GraphObject::new,
    ResourceType.FILE, GraphFile::new
  );

  // ------------------------------------------------------
  // Finder methods

  static Stream<PcdmObject> findObjects(GraphStore store, Stream<Vertex> vertices) {
    return vertices.filter(GraphResourceUtils::isObject).map((Vertex v) -> new GraphObject(store, v));
  }

  static Stream<PcdmFile> findFiles(GraphStore store, Stream<Vertex> vertices) {
    return vertices.filter(GraphResourceUtils::isFile).map((Vertex v) -> new GraphFile(store, v));
  }

  static Option<Workspace> findFirstWorkspace(GraphStore store, Stream<Vertex> vertices) {
    return vertices.find(GraphResourceUtils::isWorkspace).map((Vertex v) -> new GraphWorkspace(store, v));
  }

  static Option<PcdmCollection> findFirstCollection(Stream<Vertex> vertices, GraphStore store) {
    return vertices.find(GraphResourceUtils::isCollection).map((Vertex v) -> new GraphCollection(store, v));
  }

  static Option<PcdmObject> findFirstObject(Stream<Vertex> vertices, GraphStore store) {
    return vertices.find(GraphResourceUtils::isObject).map((Vertex v) -> new GraphObject(store, v));
  }

  // ------------------------------------------------------
  // Member methods

  static Traversable<PcdmObject> memberObjects(GraphStore store, Vertex parent) {
    return VertexUtils
      .childrenOf(parent, Labels.labelFor(ResourceType.OBJECT))
      .map((Vertex v) -> new GraphObject(store, v));
  }

  static Traversable<PcdmCollection> memberCollections(GraphStore store, Vertex parent) {
    return VertexUtils
      .childrenOf(parent, Labels.labelFor(ResourceType.COLLECTION))
      .map((Vertex v) -> new GraphCollection(store, v));
  }

  // ------------------------------------------------------
  // Creator methods

  static GraphObject createObject(GraphStore store, Vertex parent) {
    Vertex v = createChild(parent, ResourceType.OBJECT);
    return new GraphObject(store, v);
  }

  static GraphCollection createCollection(GraphStore store, Vertex parent) {
    Vertex v = createChild(parent, ResourceType.COLLECTION);
    return new GraphCollection(store, v);
  }

  static Vertex createChild(Vertex parent, ResourceType type) {
    Graph graph = parent.graph();
    Vertex child = graph.addVertex(Labels.labelFor(type));
    parent.addEdge(PARENT_CHILD, child);
    return child;
  }

  // ------------------------------------------------------
  // Misc. methods

  // TODO: benchmark this vs. relating to type nodes
  static Option<ResourceType> typeOf(Vertex vertex) {
    return Labels.resourceTypeOf(vertex.label());
  }

  static Option<AbstractGraphResource> toResource(GraphStore store, Vertex v) {
    return typeOf(v).flatMap(adapters::get).map(f -> f.apply(store, v));
  }

  // ------------------------------------------------------
  // Private methods

  private static boolean isWorkspace(Vertex v) {
    return isOfType(v, ResourceType.WORKSPACE);
  }

  private static boolean isCollection(Vertex v) {
    return isOfType(v, ResourceType.COLLECTION);
  }

  private static boolean isObject(Vertex v) {
    return isOfType(v, ResourceType.OBJECT);
  }

  private static boolean isFile(Vertex v) {
    return isOfType(v, ResourceType.FILE);
  }

  // TODO: benchmark this vs. relating to type nodes
  private static boolean isOfType(Vertex vertex, ResourceType requiredType) {
    return typeOf(vertex).contains(requiredType);
  }

  // ------------------------------------------------------
  // Constructor

  private GraphResourceUtils() {
    // private to prevent accidental instantiation
  }
}
