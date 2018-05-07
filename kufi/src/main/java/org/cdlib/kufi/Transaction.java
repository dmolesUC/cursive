package org.cdlib.kufi;

import org.cdlib.kufi.util.Ordered;
import org.cdlib.kufi.util.TimeUtil;

import java.time.ZonedDateTime;

import static org.cdlib.kufi.util.TimeUtil.utcNow;

public final class Transaction implements Ordered<Transaction> {

  // ------------------------------------------------------------
  // Fields

  private final long txid;
  private final ZonedDateTime timestamp;

  // ------------------------------------------------------------
  // Constructor

  public static Transaction initTransaction() {
    return new Transaction(0L, utcNow());
  }

  public Transaction(long txid, ZonedDateTime timestamp) {
    this.txid = txid;
    this.timestamp = TimeUtil.requireUTC(timestamp);
  }

  // ------------------------------------------------------------
  // Accessors

  public long txid() {
    return txid;
  }

  public ZonedDateTime timestamp() {
    return timestamp;
  }

  public Transaction next() {
    return new Transaction(txid + 1, utcNow());
  }

  // ------------------------------------------------------------
  // Comparable

  @Override
  public int compareTo(Transaction o) {
    if (o == this) {
      return 0;
    }
    var order = Long.compare(txid, o.txid);
    if (order != 0) {
      return order;
    }
    return timestamp.compareTo(o.timestamp);
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

    var that = (Transaction) o;
    if (txid != that.txid) {
      return false;
    }
    return timestamp.equals(that.timestamp);
  }

  @Override
  public int hashCode() {
    var result = (int) (txid ^ (txid >>> 32));
    result = 31 * result + timestamp.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Transaction(" + txid + ", " + timestamp.toInstant() + ")";
  }

  // ------------------------------------------------------------
  // Private class methods

}
