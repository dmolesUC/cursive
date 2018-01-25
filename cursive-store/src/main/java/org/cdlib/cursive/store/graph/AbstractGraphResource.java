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
    this.id = store.getId(vertex);
  }

  // ------------------------------------------------------
  // Public methods

  public Vertex vertex() {
    return vertex;
  }

  protected GraphStore store() {
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
    if (this.type() != that.type()) {
      return false;
    }
    return Objects.equals(this.vertex().id(), that.vertex().id());
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

  @Override
  public String path() {
    throw new NotImplementedException();
  }

  // ------------------------------------------------------
  // Instance methods

  Stream<Vertex> parents() {
    return VertexUtils.parentsOf(this.vertex());
  }

  Stream<Vertex> children() {
    return VertexUtils.childrenOf(this.vertex());
  }

  // ------------------------------------------------------
  // Class methods

  private static void requireTypeLabel(ResourceType requiredType, Vertex vertex) {
    Option<ResourceType> actualType = GraphResourceUtils.typeOf(vertex);
    if (!actualType.contains(requiredType)) {
      String expectedLabel = Labels.labelFor(requiredType);
      String actualLabel = vertex.label();

      String msg = actualLabel == null
        ? String.format("Expected vertex labelFor <%s>, was null", expectedLabel)
        : String.format("Expected vertex labelFor <%s>, was <%s>", expectedLabel, actualLabel);

      throw new IllegalArgumentException(msg);
    }
  }

}
