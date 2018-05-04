package org.cdlib.kufi;

import java.util.Objects;

public final class Tombstone {

  // ------------------------------------------------------------
  // Fields

  private final Transaction tx;
  private final Resource<?> resource;

  // ------------------------------------------------------------
  // Constructor

  public Tombstone(Transaction tx, Resource<?> resource) {
    this.tx = Objects.requireNonNull(tx);
    this.resource = Objects.requireNonNull(resource);
  }

  // ------------------------------------------------------------
  // Accessors

  public Transaction tx() {
    return tx;
  }

  public Resource<?> resource() {
    return resource;
  }

  // ------------------------------------------------------------
  // Object

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var tombstone = (Tombstone) o;
    if (!tx.equals(tombstone.tx)) {
      return false;
    }
    return resource.equals(tombstone.resource);
  }

  @Override
  public int hashCode() {
    var result = tx.hashCode();
    result = 31 * result + resource.hashCode();
    return result;
  }
}
