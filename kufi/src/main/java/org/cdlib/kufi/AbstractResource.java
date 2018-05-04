package org.cdlib.kufi;

import io.vavr.control.Option;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public abstract class AbstractResource<R extends Resource<R>> implements Resource<R> {

  // ------------------------------------------------------------
  // Fields

  private final ResourceType<R> type;
  private final UUID id;
  private final Transaction transaction;
  private final Version version;

  // ------------------------------------------------------------
  // Constructor

  public AbstractResource(ResourceType<R> type, UUID id, Transaction transaction, Version version) {
    this.type = Objects.requireNonNull(type);
    this.id = Objects.requireNonNull(id);
    this.transaction = Objects.requireNonNull(transaction);
    this.version = Objects.requireNonNull(version);
  }

  // ------------------------------------------------------------
  // Resource

  @Override
  public final UUID id() {
    return id;
  }

  @Override
  public final Transaction transaction() {
    return transaction;
  }

  @Override
  public final Version version() {
    return version;
  }

  @Override
  public final ResourceType<R> type() {
    return type;
  }

  @Override
  public final <R1 extends Resource<R1>> boolean hasType(ResourceType<R1> type) {
    return Objects.requireNonNull(type) == type();
  }

  @Override
  public final <R1 extends Resource<R1>> Option<R1> as(ResourceType<R1> type) {
    if (hasType(type)) {
      return some(type.cast(this));
    }
    return none();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var that = (AbstractResource<?>) o;
    if (!transaction.equals(that.transaction)) {
      return false;
    }
    if (!version.equals(that.version)) {
      return false;
    }
    if (!type.equals(that.type)) {
      return false;
    }
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    var result = type.hashCode();
    result = 31 * result + id.hashCode();
    result = 31 * result + transaction.hashCode();
    result = 31 * result + version.hashCode();
    return result;
  }
}
