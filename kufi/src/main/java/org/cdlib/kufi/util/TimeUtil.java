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
    var offset = requireNonNull(zdt).getOffset();
    if (!ZoneOffset.UTC.equals(offset)) {
      throw new IllegalArgumentException("Expected UTC datetime, got " + zdt);
    }
    return zdt;
  }
}
