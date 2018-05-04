package org.cdlib.kufi.memory;

import org.cdlib.kufi.*;

import java.util.UUID;

public class MemoryResource<R extends Resource<R>> extends AbstractResource<R> {

  final MemoryStore store;

  MemoryResource(ResourceType<R> type, UUID id, Transaction transaction, Version version, MemoryStore store) {
    super(type, id, transaction, version);
    this.store = store;
  }

}
