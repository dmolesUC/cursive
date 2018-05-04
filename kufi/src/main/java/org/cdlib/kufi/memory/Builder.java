package org.cdlib.kufi.memory;

import org.cdlib.kufi.Resource;
import org.cdlib.kufi.Transaction;
import org.cdlib.kufi.Version;

import java.util.UUID;

@FunctionalInterface
interface Builder<C extends Resource<C>> {
  C build(UUID id, Transaction txNext, Version version, MemoryStore store);
}
