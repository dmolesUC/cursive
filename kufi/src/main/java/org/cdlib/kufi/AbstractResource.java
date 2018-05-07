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
  private final Version currentVersion;
  private final Option<Version> deletedAt;

  // ------------------------------------------------------------
  // Constructor

  protected AbstractResource(ResourceType<R> type, UUID id, Version currentVersion) {
    this(type, id, currentVersion, none());
  }

  protected AbstractResource(ResourceType<R> type, UUID id, Version currentVersion, Version deletedAt) {
    this(type, id, currentVersion, some(deletedAt));
  }

  private AbstractResource(ResourceType<R> type, UUID id, Version currentVersion, Option<Version> deletedAt) {
    this.type = type;
    this.id = id;
    this.currentVersion = currentVersion;
    this.deletedAt = deletedAt;
  }

  // ------------------------------------------------------------
  // Resource

  @Override
  public final UUID id() {
    return id;
  }

  @Override
  public final Version currentVersion() {
    return currentVersion;
  }

  @Override
  public Option<Version> deletedAt() {
    return deletedAt;
  }

  @Override
  public boolean isDeleted() {
    return deletedAt.isDefined();
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
    if (!currentVersion.equals(that.currentVersion)) {
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
    result = 31 * result + currentVersion.hashCode();
    return result;
  }

}
