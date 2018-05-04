package org.cdlib.kufi;

import io.vavr.control.Option;

import java.util.Objects;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public interface Resource<R extends Resource<R>> {

  UUID id();

  Transaction transaction();

  Version version();

  ResourceType<R> type();

  <R1 extends Resource<R1>> boolean hasType(ResourceType<R1> type);

  <R1 extends Resource<R1>> Option<R1> as(ResourceType<R1> type);

}
