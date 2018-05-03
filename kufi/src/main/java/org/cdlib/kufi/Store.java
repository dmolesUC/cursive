package org.cdlib.kufi;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.UUID;

public interface Store {

  Single<Long> transaction();

  Single<Workspace> createWorkspace();

  default Completable deleteWorkspace(Workspace ws) {
    return deleteWorkspace(ws, false);
  }

  Completable deleteWorkspace(Workspace ws, boolean recursive);

  Single<Collection> createCollection(Workspace parent);

  <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type);
}
