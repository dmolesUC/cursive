package org.cdlib.kufi;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.UUID;

public interface Store {

  // ------------------------------------------------------------
  // State

  Single<Transaction> transaction();

  // ------------------------------------------------------------
  // Workspaces

  Single<Workspace> createWorkspace();

  default Single<Workspace> deleteWorkspace(Workspace ws) { // TODO: replace Completables with tombstones
    return deleteWorkspace(ws, false);
  }

  Single<Workspace> deleteWorkspace(Workspace ws, boolean recursive);

  // ------------------------------------------------------------
  // Collections

  Single<Collection> createCollection(Workspace parent);

  Single<Collection> createCollection(Collection parent);

  default Single<Collection> deleteCollection(Collection ws) {
    return deleteCollection(ws, false);
  }

  Single<Collection> deleteCollection(Collection ws, boolean recursive);

  // ------------------------------------------------------------
  // Finders

  Maybe<Resource<?>> find(UUID id);

  Maybe<Resource<?>> findTombstone(UUID id);

  <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type);

  <R extends Resource<R>> Maybe<R> findTombstone(UUID id, ResourceType<R> type);
}
