package org.cdlib.cursive.store.memory;

import io.vavr.Lazy;
import org.cdlib.cursive.pcdm.PcdmResource;

import java.util.Objects;

class PcdmResourceImpl implements PcdmResource {
  private final String identifier;
  private final Lazy<String> stringVal = Lazy.of(() -> getClass().getName() + "<" + identifier() + ">");

  PcdmResourceImpl(String identifier) {
    Objects.requireNonNull(identifier, () -> String.format("%s must have an identifier", getClass().getSimpleName()));
    this.identifier = identifier;
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PcdmResourceImpl that = (PcdmResourceImpl) o;
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
