package org.cdlib.cursive.store.graph;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import java.util.Iterator;
import java.util.function.Function;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.V;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class VertexUtils {

  // ------------------------------------------------------
  // Class fields

  private static final Map<ResourceType, Function<Vertex, AbstractGraphResource>> adapters = HashMap.of(
    ResourceType.WORKSPACE, GraphWorkspace::new,
    ResourceType.COLLECTION, GraphCollection::new,
    ResourceType.OBJECT, GraphObject::new,
    ResourceType.FILE, GraphObject::new
  );

  // ------------------------------------------------------
  // Class methods

  static Stream<Vertex> parentsOf(Vertex child) {
    return Stream.ofAll(() -> child.vertices(Direction.IN, Labels.PARENT_CHILD));
  }

  static Stream<Vertex> childrenOf(Vertex parent) {
    return Stream.ofAll(() -> parent.vertices(Direction.OUT, Labels.PARENT_CHILD));
  }

  // TODO: something graph-native & smarter
  static Stream<Vertex> descendantsOf(Vertex parent) {
    Stream<Vertex> selfStream = Stream.of(parent);
    Iterator<Vertex> children = parent.vertices(Direction.OUT, Labels.PARENT_CHILD);
    if (!children.hasNext()) {
      return selfStream;
    }
    return selfStream.appendAll(Stream.ofAll(() -> children).flatMap(VertexUtils::descendantsOf));
  }

  static Stream<Workspace> findWorkspaces(Stream<Vertex> vertices) {
    return vertices.filter(VertexUtils::isWorkspace).map(GraphWorkspace::new);
  }

  static Stream<PcdmCollection> findCollections(Stream<Vertex> vertices) {
    return vertices.filter(VertexUtils::isCollection).map(GraphCollection::new);
  }

  static Stream<PcdmObject> findObjects(Stream<Vertex> vertices) {
    return vertices.filter(VertexUtils::isObject).map(GraphObject::new);
  }

  static Stream<PcdmFile> findFiles(Stream<Vertex> vertices) {
    return vertices.filter(VertexUtils::isFile).map(GraphFile::new);
  }

  static Option<Workspace> findFirstWorkspace(Stream<Vertex> vertices) {
    return vertices.find(VertexUtils::isWorkspace).map(GraphWorkspace::new);
  }

  static Option<PcdmCollection> findFirstCollection(Stream<Vertex> vertices) {
    return vertices.find(VertexUtils::isCollection).map(GraphCollection::new);
  }

  static Option<PcdmObject> findFirstObject(Stream<Vertex> vertices) {
    return vertices.find(VertexUtils::isObject).map(GraphObject::new);
  }

  // TODO: benchmark this vs. relating to type nodes
  static Option<ResourceType> typeOf(Vertex vertex) {
    return Labels.resourceTypeOf(vertex.label());
  }

  // TODO: benchmark this vs. relating to type nodes
  static boolean isOfType(Vertex vertex, ResourceType requiredType) {
    return typeOf(vertex).contains(requiredType);
  }

  static boolean isWorkspace(Vertex v) {
    return isOfType(v, ResourceType.WORKSPACE);
  }

  static boolean isCollection(Vertex v) {
    return isOfType(v, ResourceType.COLLECTION);
  }

  static boolean isObject(Vertex v) {
    return isOfType(v, ResourceType.OBJECT);
  }

  static boolean isFile(Vertex v) {
    return isOfType(v, ResourceType.FILE);
  }

  static Vertex createChild(Vertex parent, ResourceType type) {
    Graph graph = parent.graph();
    Vertex child = graph.addVertex(Labels.labelFor(type));
    parent.addEdge(Labels.PARENT_CHILD, child);
    return child;
  }

  static Option<AbstractGraphResource> toResource(Vertex v) {
    return typeOf(v).flatMap(adapters::get).map(f -> f.apply(v));
  }

  // ------------------------------------------------------
  // Constructor

  private VertexUtils() {
    // private to prevent accidental instantiation
  }

  public static void main(String[] args) {
    GraphStore store = new GraphStore();
    store.root().property("name", "root");

    GraphObject o0 = store.createObject();
    o0.vertex.property("name", "o0");

    GraphObject o01 = o0.createObject();
    o01.vertex.property("name", "o0.o1");

    GraphWorkspace w1 = store.createWorkspace();
    w1.vertex.property("name", "w1");

    GraphWorkspace w2 = store.createWorkspace();
    w2.vertex.property("name", "w2");

    GraphCollection w1c1 = w1.createCollection();
    w1c1.vertex.property("name", "w1.c1");

    GraphCollection w2c2 = w2.createCollection();
    w2c2.vertex.property("name", "w2.c2");

    GraphObject w1c1o1 = w1c1.createObject();
    w1c1o1.vertex.property("name", "w1.c1.o1");

    GraphObject w2c2o2 = w2c2.createObject();
    w2c2o2.vertex.property("name", "w2.c2.o2");

    for (
      GraphTraversal<Object, Vertex> t = V(store.root().id()).repeat(out()).emit();
      t.hasNext();
      ) {
      Vertex next = t.next();
      System.out.println(next.property("name"));
    }
  }
}
