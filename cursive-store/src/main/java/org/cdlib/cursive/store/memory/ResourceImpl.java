package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import org.apache.commons.lang.NotImplementedException;
import org.cdlib.cursive.core.Resource;

import java.util.Objects;
import java.util.UUID;

abstract class ResourceImpl implements Resource {
  private final UUID identifier;
  private final Lazy<String> stringVal = Lazy.of(() -> getClass().getName() + "<" + id() + ">");

  ResourceImpl(UUID identifier) {
    Objects.requireNonNull(identifier, () -> String.format("%s must have an identifier", getClass().getSimpleName()));
    this.identifier = identifier;
  }

  public UUID id() {
    return identifier;
  }

  @Override
  public String path() {
    throw new NotImplementedException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResourceImpl that = (ResourceImpl) o;
    return identifier.equals(that.identifier);
  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }

  @Override
  public String toString() {
    return stringVal.get();
  }
}
