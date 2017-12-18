package org.cdlib.cursive.store.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;

import java.util.Objects;

class GraphRelation implements PcdmRelation {

  private final Edge edge;

  GraphRelation(Edge edge) {
    Objects.requireNonNull(edge);
    this.edge = edge;
  }

  @Override
  public PcdmObject fromObject() {
    return new GraphObject(edge.outVertex());
  }

  @Override
  public PcdmObject toObject() {
    return new GraphObject(edge.inVertex());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(edge.id());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GraphRelation that = (GraphRelation) o;
    return Objects.equals(edge.id(), that.edge.id());
  }
}
