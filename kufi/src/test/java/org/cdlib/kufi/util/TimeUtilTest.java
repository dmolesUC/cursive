package org.cdlib.kufi.util;

import io.vavr.collection.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class TimeUtilTest {

  private static List<ZoneId> zones = List.ofAll(ZoneOffset.getAvailableZoneIds()).map(ZoneId::of);

  private static List<ZoneId> zones() {
    return zones;
  }

  @ParameterizedTest
  @MethodSource("org.cdlib.kufi.util.TimeUtilTest#zones")
  void requireUTCRequiresUTC(ZoneId zone) {
    ThrowingCallable callingRequireUTC = () -> TimeUtil.requireUTC(ZonedDateTime.now(zone));
    if (ZoneOffset.UTC.equals(zone)) {
      assertThatCode(callingRequireUTC).doesNotThrowAnyException();
    } else {
      assertThatIllegalArgumentException().isThrownBy(callingRequireUTC);
    }
  }
}
