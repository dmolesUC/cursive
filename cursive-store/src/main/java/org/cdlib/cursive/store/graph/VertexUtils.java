package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.stream.StreamSupport;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;
import static org.cdlib.cursive.store.graph.Labels.PARENT_CHILD;

class VertexUtils {

  static Stream<Vertex> parentsOf(Vertex child) {
    return Stream.ofAll(() -> child.vertices(Direction.IN, PARENT_CHILD));
  }

  static Stream<Vertex> childrenOf(Vertex parent) {
    return Stream.ofAll(() -> parent.vertices(Direction.OUT, PARENT_CHILD));
  }

  static Stream<Vertex> childrenOf(Vertex parent, String label) {
    GraphTraversal<Vertex, Vertex> traversal = parent.graph().traversal().V(parent)
      .out(PARENT_CHILD)
      .hasLabel(label);
    return Stream.ofAll(() -> traversal);
  }

  static Stream<Vertex> descendantsOf(Vertex parent) {
    GraphTraversal<Vertex, Vertex> traversal = parent.graph().traversal().V(parent)
      .repeat(out(PARENT_CHILD))
      .emit();
    return Stream.ofAll(() -> traversal);
  }

  // TODO: benchmark this vs. adding type nodes & relating all vertices of type to those nodes
  static Stream<Vertex> descendantsOf(Vertex parent, String label) {
    GraphTraversal<Vertex, Vertex> traversal = parent.graph().traversal().V(parent)
      .repeat(out(PARENT_CHILD))
      .emit()
      .hasLabel(label);
    StreamSupport.stream(((Iterable<Vertex>)() -> traversal).spliterator(), false);
    return Stream.ofAll(() -> traversal);
  }

  // ------------------------------------------------------
  // Constructor

  private VertexUtils() {
    // private to prevent accidental instantiation
  }
}
