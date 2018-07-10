package org.cdlib.kufi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.cdlib.kufi.util.TimeUtil.utcNow;

class VersionTest {

  private static SecureRandom r;

  @BeforeAll
  static void setUp() {
    r = new SecureRandom();
  }

  @Nested
  class CompareTo {
    private Transaction tx;

    @BeforeEach
    void setUp() {
      tx = new Transaction(r.nextLong(), utcNow());
    }

    @Test
    void versionsAreEqualToThemselves() {
      var v = new Version(r.nextLong(), tx);
      assertThat(v).isEqualByComparingTo(v);
    }

    @Test
    void versionsAreNotEqualToNull() {
      var v = new Version(r.nextLong(), tx);
      assertThatNullPointerException().isThrownBy(() -> v.compareTo(null));
    }

    @Test
    void versionsAreEqualToIdenticalVersions() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(v0.vid(), tx);
      assertThat(v0).isEqualByComparingTo(v1);
      assertThat(v1).isEqualByComparingTo(v0);
    }

    @Test
    void versionsSortByNumber() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(1 + v0.vid(), v0.transaction());
      assertThat(v0).isLessThan(v1);
      assertThat(v1).isGreaterThan(v0);
    }

    @Test
    void versionsSortByTransaction() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(v0.vid(), tx.next());
      assertThat(v0).isLessThan(v1);
      assertThat(v1).isGreaterThan(v0);
    }
  }

  @Nested
  class Equals {
    private Transaction tx;

    @BeforeEach
    void setUp() {
      tx = new Transaction(r.nextLong(), utcNow());
    }

    @Test
    void versionsAreEqualToThemselves() {
      var v = new Version(r.nextLong(), tx);
      assertThat(v).isEqualTo(v);
    }

    @Test
    void versionsAreNotEqualToNull() {
      var v = new Version(r.nextLong(), tx);
      assertThat(v).isNotEqualTo(null);
    }

    @Test
    void versionsAreEqualToIdenticalVersions() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(v0.vid(), tx);
      assertThat(v0).isEqualTo(v1);
      assertThat(v1).isEqualTo(v0);
    }

    @Test
    void versionsWithDifferentNumbersAreNotEqual() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(1 + v0.vid(), v0.transaction());
      assertThat(v0).isNotEqualTo(v1);
      assertThat(v1).isNotEqualTo(v0);
    }

    @Test
    void versionsWithDifferentTransactionsAreNotEqual() {
      var v0 = new Version(r.nextLong(), tx);
      var v1 = new Version(v0.vid(), tx.next());
      assertThat(v0).isNotEqualTo(v1);
      assertThat(v1).isNotEqualTo(v0);
    }
  }
}
