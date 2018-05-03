package org.cdlib.kufi.memory;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.kufi.*;

import java.util.NoSuchElementException;
import java.util.UUID;

import static io.reactivex.Single.just;

public class MemoryStore implements Store {

  // ------------------------------------------------------------
  // Instance fields

  private final Object mutex = new Object();
  private volatile StoreState state = new StoreState();

  // ------------------------------------------------------------
  // Store

  @Override
  public Single<Long> transaction() {
    return just(state.transaction());
  }

  @Override
  public Single<Workspace> createWorkspace() {
    synchronized (mutex) {
      var result = state.createWorkspace(this);
      state = result.state();
      return just(result.value());
    }
  }

  @Override
  public Single<Collection> createCollection(Workspace parent) {
    synchronized (mutex) {
      var result = state.createCollection(this, (MemoryWorkspace) parent);
      state = result.state();
      return just(result.value());
    }
  }

  @Override
  public <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type) {
    return state.find(id).flatMap(type::cast).map(Maybe::just).getOrElse(Maybe.empty());
  }

  // ------------------------------------------------------------
  // Package-private

  <R extends Resource<R>> Observable<R> findChildrenOfType(Resource<?> parent, ResourceType<R> type) {
    var children = state.findChildrenOfType(parent.id(), type);
    return Observable.fromIterable(children);
  }

  Single<Resource<?>> findParentOf(Resource<?> child) {
    var parent = state.findParent(child.id())
      .getOrElseThrow(() -> new NoSuchElementException("No parent found for resource: " + child));
    return Single.just(parent);
  }
}
