package org.cdlib.kufi;

import io.vavr.control.Option;

public interface Link {
  LinkType type();

  Resource<?> source();

  Resource<?> target();

  Transaction createdAt();

  Option<Transaction> deletedAt();

  boolean isLive();

  boolean isDeleted();
}
