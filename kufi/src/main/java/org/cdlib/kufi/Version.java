package org.cdlib.kufi;

import org.cdlib.kufi.util.Ordered;

import java.util.Objects;

public final class Version implements Ordered<Version> {

  // ------------------------------------------------------------
  // Fields

  private final long vid;
  private final Transaction transaction;

  // ------------------------------------------------------------
  // Constructor

  public static Version initVersion(Transaction txNext) {
    return new Version(0L, txNext);
  }

  public Version(long vid, Transaction transaction) {
    this.vid = vid;
    this.transaction = Objects.requireNonNull(transaction);
  }

  // ------------------------------------------------------------
  // Accessors

  public long vid() {
    return vid;
  }

  public Transaction transaction() {
    return transaction;
  }

  public Version next(Transaction txNext) {
    return new Version(vid + 1, txNext);
  }

  // ------------------------------------------------------------
  // Comparable

  @Override
  public int compareTo(Version o) {
    if (o == this) {
      return 0;
    }
    var order = Long.compare(vid, o.vid);
    if (order != 0) {
      return order;
    }
    return transaction.compareTo(o.transaction);
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

    var that = (Version) o;
    if (vid != that.vid) {
      return false;
    }
    return transaction.equals(that.transaction);
  }

  @Override
  public int hashCode() {
    var result = (int) (vid ^ (vid >>> 32));
    result = 31 * result + transaction.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Version(" + vid + ", " + transaction + ")";
  }
}
