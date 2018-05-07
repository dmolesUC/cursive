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

  default Completable deleteWorkspace(Workspace ws) {
    return deleteWorkspace(ws, false);
  }

  Completable deleteWorkspace(Workspace ws, boolean recursive);

  // ------------------------------------------------------------
  // Collections

  Single<Collection> createCollection(Workspace parent);

  Single<Collection> createCollection(Collection parent);

  default Completable deleteCollection(Collection ws) {
    return deleteCollection(ws, false);
  }

  Completable deleteCollection(Collection ws, boolean recursive);

  // ------------------------------------------------------------
  // Finders

  Maybe<Resource<?>> find(UUID id);

  Maybe<Tombstone<?>> findTombstone(UUID id);

  <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type);

  <R extends Resource<R>> Maybe<Tombstone<R>> findTombstone(UUID id, ResourceType<R> type);
}
