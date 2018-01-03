package org.cdlib.cursive.store.graph;

import io.vavr.Lazy;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;

import java.util.Objects;

abstract class AbstractGraphResource implements Resource {

  // ------------------------------------------------------
  // Instance fields

  private final Vertex vertex;
  private final Lazy<String> stringVal = Lazy.of(() -> getClass().getName() + "<" + identifier() + ">");

  // ------------------------------------------------------
  // Constructors

  AbstractGraphResource(ResourceType resourceType, Vertex vertex) {
    Objects.requireNonNull(resourceType);
    Objects.requireNonNull(vertex);
    requireTypeLabel(resourceType, vertex);
    this.vertex = vertex;
  }

  // ------------------------------------------------------
  // Public methods

  public Vertex vertex() {
    return vertex;
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
  public String identifier() {
    return vertex().id().toString();
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
