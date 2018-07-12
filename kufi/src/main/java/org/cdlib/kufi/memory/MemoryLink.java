package org.cdlib.kufi.memory;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.cdlib.kufi.Link;
import org.cdlib.kufi.LinkType;
import org.cdlib.kufi.Transaction;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.cdlib.kufi.util.Preconditions.checkArgument;

public final class MemoryLink implements Link {

  // ------------------------------------------------------------
  // Fields

  private final LinkType type;
  private final MemoryResource<?> source;
  private final MemoryResource<?> target;
  private final Transaction createdAt;
  private final Option<Transaction> deletedAt;

  // ------------------------------------------------------------
  // Constructor

  static MemoryLink create(MemoryResource<?> source, LinkType type, MemoryResource<?> target, Transaction createdAt) {
    return new MemoryLink(source, type, target, createdAt, none());
  }

  MemoryLink deleted(MemoryResource<?> sourceNext, MemoryResource<?> targetNext, Transaction deletedAt) {
    return create(
      checkArgument(sourceNext, (r) -> r.isLaterVersionOf(source), () -> "Expected source to be later version of: " + source + "; got " + sourceNext),
      type,
      checkArgument(targetNext, (r) -> r.isLaterVersionOf(target), () -> "Expected target to be later version of: " + target + "; got " + targetNext),
      createdAt,
      deletedAt
    );
  }

  private static MemoryLink create(MemoryResource<?> source, LinkType type, MemoryResource<?> target, Transaction createdAt, Transaction deletedAt) {
    return new MemoryLink(source, type, target, createdAt, some(deletedAt));
  }

  private MemoryLink(MemoryResource<?> source, LinkType type, MemoryResource<?> target, Transaction createdAt, Option<Transaction> deletedAt) {
    this.type = Objects.requireNonNull(type);
    this.source = Objects.requireNonNull(source);
    this.target = Objects.requireNonNull(target);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.deletedAt = Objects.requireNonNull(deletedAt);
  }

  // ------------------------------------------------------------
  // Link

  @Override
  public LinkType type() {
    return type;
  }

  @Override
  public MemoryResource<?> source() {
    return source;
  }

  @Override
  public MemoryResource<?> target() {
    return target;
  }

  @Override
  public Transaction createdAt() {
    return createdAt;
  }

  @Override
  public Option<Transaction> deletedAt() {
    return deletedAt;
  }

  @Override
  public boolean isLive() {
    return !isDeleted();
  }

  @Override
  public boolean isDeleted() {
    return deletedAt.isDefined();
  }

  // ------------------------------------------------------------
  // Accessors

  UUID sourceId() {
    return source.id();
  }

  UUID targetId() {
    return target.id();
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
    if (!type.equals(link.type())) {
      return false;
    }
    if (!source.equals(link.source())) {
      return false;
    }
    if (!target.equals(link.target())) {
      return false;
    }
    if (!createdAt.equals(link.createdAt())) {
      return false;
    }
    return deletedAt.equals(link.deletedAt());
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
