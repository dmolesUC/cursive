package org.cdlib.kufi;

import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.cdlib.kufi.util.Preconditions.checkArgument;

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

  public static Link create(Resource<?> source, LinkType type, Resource<?> target, Transaction createdAt, Transaction deletedAt) {
    return new Link(source, type, target, createdAt, some(deletedAt));
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

  public UUID targetId() {
    return target.id();
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

  public Link deleted(Resource<?> sourceNext, Resource<?> targetNext, Transaction deletedAt) {
    return create(
      checkArgument(sourceNext, (r) -> r.isLaterVersionOf(source), () -> "Expected source to be later version of: " + source + "; got " + sourceNext),
      type,
      checkArgument(targetNext, (r) -> r.isLaterVersionOf(target), () -> "Expected target to be later version of: " + target + "; got " + targetNext),
      createdAt,
      deletedAt
    );
  }

  // ------------------------------------------------------------
  // Object


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }

    var link = (Link) o;
    if (!type.equals(link.type)) {
      return false;
    }
    if (!source.equals(link.source)) {
      return false;
    }
    if (!target.equals(link.target)) {
      return false;
    }
    if (!createdAt.equals(link.createdAt)) {
      return false;
    }
    return deletedAt.equals(link.deletedAt);
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

  @Override
  public String toString() {
    return List.of(source, type, target, createdAt, deletedAt).mkString("Link(", ", ", ")");
  }
}
