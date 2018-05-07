package org.cdlib.kufi.memory;

import io.vavr.control.Option;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.Version;

import java.util.UUID;

@FunctionalInterface
interface ResourceConstructor<C extends Resource<C>> {
  C construct(UUID id, Version currentVersion, Option<Version> deletedAt, MemoryStore store);
}

