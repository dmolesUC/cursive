package org.cdlib.kufi;

import org.cdlib.kufi.util.TimeUtil;

import java.time.ZonedDateTime;

import static org.cdlib.kufi.util.TimeUtil.utcNow;

public final class Version implements Comparable<Version> {

  // ------------------------------------------------------------
  // Fields

  private final long vid;
  private final ZonedDateTime timestamp;

  // ------------------------------------------------------------
  // Constructor

  public static Version initVersion() {
    return new Version(0L, utcNow());
  }

  public Version(long vid, ZonedDateTime timestamp) {
    this.vid = vid;
    this.timestamp = TimeUtil.requireUTC(timestamp);
  }

  // ------------------------------------------------------------
  // Accessors

  public long vid() {
    return vid;
  }

  public ZonedDateTime timestamp() {
    return timestamp;
  }

  public Version next() {
    return new Version(vid + 1, utcNow());
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

    var that = (Version) o;
    if (vid != that.vid) {
      return false;
    }
    return timestamp.equals(that.timestamp);
  }

  @Override
  public int hashCode() {
    var result = (int) (vid ^ (vid >>> 32));
    result = 31 * result + timestamp.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Version(" + vid + ", " + timestamp + ")";
  }
}
