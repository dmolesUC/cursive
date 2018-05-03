package org.cdlib.kufi.memory;

import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;

import java.util.UUID;

public class MemoryResource<R extends Resource<R>> implements Resource<R> {

  private final ResourceType<R> type;
  private final UUID id;
  private final long transaction;
  private final long version;

  final MemoryStore store;

  MemoryResource(ResourceType<R> type, UUID id, long transaction, long version, MemoryStore store) {
    this.type = type;
    this.id = id;
    this.transaction = transaction;
    this.version = version;
    this.store = store;
  }

  @Override
  public UUID id() {
    return id;
  }

  @Override
  public long transaction() {
    return transaction;
  }

  @Override
  public long version() {
    return version;
  }

  @Override
  public ResourceType<R> type() {
    return type;
  }
}
