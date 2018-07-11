package org.cdlib.kufi.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public class TimeUtil {

  private TimeUtil() {
    // private to prevent instantiation
  }

  public static ZonedDateTime utcNow() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  public static ZonedDateTime requireUTC(ZonedDateTime zdt) {
    var zone = requireNonNull(zdt).getZone();
    if (!ZoneOffset.UTC.equals(zone)) {
      throw new IllegalArgumentException("Expected UTC datetime, got " + zdt);
    }
    return zdt;
  }
}
