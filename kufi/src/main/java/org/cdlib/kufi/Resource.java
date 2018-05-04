package org.cdlib.kufi;

import io.vavr.control.Option;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public interface Resource<R extends Resource<R>> {

  // ------------------------------------------------------------
  // Interface methods

  UUID id();

  long transaction();

  long version();

  ResourceType<R> type();

  // ------------------------------------------------------------
  // Class methods

  default <R1 extends Resource<R1>> boolean hasType(ResourceType<R1> type) {
    Objects.requireNonNull(type);
    return type() == type;
  }

  default <R1 extends Resource<R1>> Option<R1> as(ResourceType<R1> type) {
    if (hasType(type)) {
      return some(type.cast(this));
    }
    return none();
  }

}
