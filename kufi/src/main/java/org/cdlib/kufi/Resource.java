package org.cdlib.kufi;

import java.util.UUID;

public interface Resource<R extends Resource<R>> {
  UUID id();

  long transaction();

  long version();

  ResourceType<R> type();
}
