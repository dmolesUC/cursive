package org.cdlib.kufi;

import io.vavr.control.Option;

import java.util.UUID;

public interface Resource<R extends Resource<R>> {

  UUID id();

  Version version();

  ResourceType<R> type();

  <R1 extends Resource<R1>> boolean hasType(ResourceType<R1> type);

  <R1 extends Resource<R1>> Option<R1> as(ResourceType<R1> type);

}
