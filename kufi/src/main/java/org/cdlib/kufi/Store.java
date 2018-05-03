package org.cdlib.kufi;

import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.UUID;

public interface Store {

  Single<Long> transaction();

  Single<Workspace> createWorkspace();

  Single<Collection> createCollection(Workspace parent);

  <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type);
}
