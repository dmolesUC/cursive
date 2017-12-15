package org.cdlib.cursive.store.graph;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;

abstract class AbstractGraphResource implements Resource {

  protected final Vertex vertex;

  AbstractGraphResource(ResourceType resourceType, Vertex vertex) {
    this.vertex = vertex;
    requireTypeLabel(resourceType, vertex);
  }

  // ------------------------------------------------------
  // Resource

  @Override
  public String identifier() {
    return vertex.id().toString();
  }

  // ------------------------------------------------------
  // Instance methods

  Stream<Vertex> parents() {
    return VertexUtils.parentsOf(this.vertex);
  }

  Stream<Vertex> children() {
    return VertexUtils.childrenOf(this.vertex);
  }

  // ------------------------------------------------------
  // Class methods

  private static void requireTypeLabel(ResourceType requiredType, Vertex vertex) {
    Option<ResourceType> actualType = VertexUtils.typeOf(vertex);
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
