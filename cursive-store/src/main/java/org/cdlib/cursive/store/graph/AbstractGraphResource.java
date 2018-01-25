package org.cdlib.cursive.store.graph;

import io.vavr.Lazy;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.commons.lang.NotImplementedException;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;

import java.util.Objects;
import java.util.UUID;

abstract class AbstractGraphResource implements Resource {

  // ------------------------------------------------------
  // Instance fields

  private final Vertex vertex;
  private final Lazy<String> stringVal = Lazy.of(() -> getClass().getName() + "<" + id() + ">");
  private final UUID id;
  private final GraphStore store;

  // ------------------------------------------------------
  // Constructors

  AbstractGraphResource(ResourceType resourceType, GraphStore store, Vertex vertex) {
    Objects.requireNonNull(resourceType);
    Objects.requireNonNull(store);
    Objects.requireNonNull(vertex);
    requireTypeLabel(resourceType, vertex);
    this.vertex = vertex;
    this.store = store;
    id = store.getId(vertex);
  }

  // ------------------------------------------------------
  // Package-local methods

  Vertex vertex() {
    return vertex;
  }

  GraphStore store() {
    return store;
  }

  // ------------------------------------------------------
  // Object

  @Override
  public int hashCode() {
    return vertex().id().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractGraphResource that = (AbstractGraphResource) o;
    if (type() != that.type()) {
      return false;
    }
    return Objects.equals(vertex().id(), that.vertex().id());
  }

  // ------------------------------------------------------
  // Resource

  @Override
  public String toString() {
    return stringVal.get();
  }

  @Override
  public UUID id() {
    return id;
  }

  // ------------------------------------------------------
  // Instance methods

  Stream<Vertex> parents() {
    return VertexUtils.parentsOf(vertex());
  }

  Stream<Vertex> children() {
    return VertexUtils.childrenOf(vertex());
  }

  // ------------------------------------------------------
  // Class methods

  private static void requireTypeLabel(ResourceType requiredType, Vertex vertex) {
    Option<ResourceType> actualType = GraphResourceUtils.typeOf(vertex);
    if (!actualType.contains(requiredType)) {
      String expectedLabel = Labels.labelFor(requiredType);
      String actualLabel = vertex.label();

      String msg = actualLabel == null
        ? String.format("Expected vertex label <%s>, was null", expectedLabel)
        : String.format("Expected vertex label <%s>, was <%s>", expectedLabel, actualLabel);

      throw new IllegalArgumentException(msg);
    }
  }

}
