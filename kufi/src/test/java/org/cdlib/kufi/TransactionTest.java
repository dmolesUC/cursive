package org.cdlib.kufi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.cdlib.kufi.util.TimeUtil.utcNow;

class TransactionTest {

  private static SecureRandom r;

  @BeforeAll
  static void setUp() {
    r = new SecureRandom();
  }

  @Nested
  class CompareTo {
    private ZonedDateTime timestamp;

    @BeforeEach
    void setUp() {
      timestamp = utcNow();
    }

    @Test
    void transactionsAreEqualToThemselves() {
      var tx = new Transaction(r.nextLong(), timestamp);
      assertThat(tx).isEqualByComparingTo(tx);
    }

    @Test
    void transactionsAreNotEqualToNull() {
      var tx = new Transaction(r.nextLong(), timestamp);
      assertThatNullPointerException().isThrownBy(() -> tx.compareTo(null));
    }

    @Test
    void transactionsAreEqualToIdenticalTransactions() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(tx0.txid(), timestamp);
      assertThat(tx0).isEqualByComparingTo(tx1);
      assertThat(tx1).isEqualByComparingTo(tx0);
    }

    @Test
    void transactionsSortByNumber() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(1 + tx0.txid(), tx0.timestamp());
      assertThat(tx0).isLessThan(tx1);
      assertThat(tx1).isGreaterThan(tx0);
    }

    @Test
    void transactionsSortByDate() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(tx0.txid(), timestamp.plus(1, ChronoUnit.NANOS));
      assertThat(tx0).isLessThan(tx1);
      assertThat(tx1).isGreaterThan(tx0);
    }
  }

  @Nested
  class Equals {
    private ZonedDateTime timestamp;

    @BeforeEach
    void setUp() {
      timestamp = utcNow();
    }

    @Test
    void transactionsAreEqualToThemselves() {
      var tx = new Transaction(r.nextLong(), timestamp);
      assertThat(tx).isEqualTo(tx);
    }

    @Test
    void transactionsAreNotEqualToNull() {
      var tx = new Transaction(r.nextLong(), timestamp);
      assertThat(tx).isNotEqualTo(null);
    }

    @Test
    void transactionsAreEqualToIdenticalTransactions() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(tx0.txid(), timestamp);
      assertThat(tx0).isEqualTo(tx1);
      assertThat(tx1).isEqualTo(tx0);
    }

    @Test
    void transactionsWithDifferentNumbersAreNotEqual() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(1 + tx0.txid(), tx0.timestamp());
      assertThat(tx0).isNotEqualTo(tx1);
      assertThat(tx1).isNotEqualTo(tx0);
    }

    @Test
    void transactionsWithDifferentDatesAreNotEqual() {
      var tx0 = new Transaction(r.nextLong(), timestamp);
      var tx1 = new Transaction(tx0.txid(), timestamp.plus(1, ChronoUnit.NANOS));
      assertThat(tx0).isNotEqualTo(tx1);
      assertThat(tx1).isNotEqualTo(tx0);
    }
  }
}
