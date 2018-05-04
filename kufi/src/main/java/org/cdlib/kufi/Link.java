package org.cdlib.kufi;

import io.vavr.control.Option;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public final class Link {

  // ------------------------------------------------------------
  // Fields

  private final LinkType type;
  private final Resource<?> source;
  private final Resource<?> target;
  private final Transaction createdAt;
  private final Option<Transaction> deletedAt;

  // ------------------------------------------------------------
  // Constructor

  public static Link create(Resource<?> source, LinkType type, Resource<?> target, Transaction createdAt) {
    return new Link(source, type, target, createdAt, none());
  }

  private Link(Resource<?> source, LinkType type, Resource<?> target, Transaction createdAt, Option<Transaction> deletedAt) {
    this.type = Objects.requireNonNull(type);
    this.source = Objects.requireNonNull(source);
    this.target = Objects.requireNonNull(target);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.deletedAt = Objects.requireNonNull(deletedAt);
  }

  // ------------------------------------------------------------
  // Accessors

  public LinkType type() {
    return type;
  }

  public Resource<?> source() {
    return source;
  }

  public UUID sourceId() {
    return source.id();
  }

  public Resource<?> target() {
    return target;
  }

  public Transaction createdAt() {
    return createdAt;
  }

  public Option<Transaction> deletedAt() {
    return deletedAt;
  }

  public boolean isLive() {
    return !isDeleted();
  }

  public boolean isDeleted() {
    return deletedAt.isDefined();
  }

  public Link deleted(Transaction deletedAt) {
    return new Link(source, type, target, createdAt, some(deletedAt));
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

    var relation = (Link) o;
    if (!type.equals(relation.type)) {
      return false;
    }
    if (!source.equals(relation.source)) {
      return false;
    }
    if (!target.equals(relation.target)) {
      return false;
    }
    if (!createdAt.equals(relation.createdAt)) {
      return false;
    }
    return deletedAt.equals(relation.deletedAt);
  }

  @Override
  public int hashCode() {
    var result = type.hashCode();
    result = 31 * result + source.hashCode();
    result = 31 * result + target.hashCode();
    result = 31 * result + createdAt.hashCode();
    result = 31 * result + deletedAt.hashCode();
    return result;
  }
}
