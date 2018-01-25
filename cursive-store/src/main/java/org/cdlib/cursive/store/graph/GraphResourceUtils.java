package org.cdlib.cursive.store.graph;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;

import java.util.function.BiFunction;

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

  private static boolean isOfType(Vertex vertex, ResourceType requiredType) {
    return typeOf(vertex).contains(requiredType);
  }

  // ------------------------------------------------------
  // Constructor

  private GraphResourceUtils() {
    // private to prevent accidental instantiation
  }
}
