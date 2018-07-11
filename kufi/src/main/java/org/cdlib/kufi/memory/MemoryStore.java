package org.cdlib.kufi.memory;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.kufi.*;

import java.util.NoSuchElementException;
import java.util.UUID;

import static io.reactivex.Single.just;
import static org.cdlib.kufi.ResourceType.COLLECTION;

public class MemoryStore implements Store {

  // ------------------------------------------------------------
  // Instance fields

  private final Object mutex = new Object();
  private volatile StoreState state;

  // ------------------------------------------------------------
  // Constructor

  public MemoryStore() {
    this(new StoreState());
  }

  MemoryStore(StoreState initialState) {
    state = initialState;
  }

  // ------------------------------------------------------------
  // Store

  @Override
  public Single<Transaction> transaction() {
    return just(state.transaction());
  }

  @Override
  public Single<Workspace> createWorkspace() {
    synchronized (mutex) {
      try {
        var result = state.createWorkspace(this);
        state = result.stateNext();
        return just(result.resource());
      } catch (Exception e) {
        return Single.error(e);
      }
    }
  }

  @Override
  public Single<Workspace> deleteWorkspace(Workspace ws, boolean recursive) {
    return delete(ws, recursive);
  }

  @Override
  public Single<Collection> createCollection(Workspace parent) {
    return create(parent, COLLECTION);
  }

  @Override
  public Single<Collection> createCollection(Collection parent) {
    return create(parent, COLLECTION);
  }

  @Override
  public Single<Collection> deleteCollection(Collection coll, boolean recursive) {
    return delete(coll, recursive);
  }

  @Override
  public Maybe<Resource<?>> find(UUID id) {
    try {
      return state.find(id)
        .<Maybe<Resource<?>>>map(Maybe::just)
        .getOrElse(Maybe::empty);
    } catch (Exception e) {
      return Maybe.error(e);
    }
  }

  @Override
  public Maybe<Resource<?>> findTombstone(UUID id) {
    try {
      return state.findTombstone(id)
        .<Maybe<Resource<?>>>map(Maybe::just)
        .getOrElse(Maybe::empty);
    } catch (Exception e) {
      return Maybe.error(e);
    }
  }

  @Override
  public <R extends Resource<R>> Maybe<R> find(UUID id, ResourceType<R> type) {
    try {
      return state.find(id)
        .flatMap(r1 -> r1.as(type))
        .map(Maybe::just)
        .getOrElse(Maybe::empty);
    } catch (Exception e) {
      return Maybe.error(e);
    }
  }

  @Override
  public <R extends Resource<R>> Maybe<R> findTombstone(UUID id, ResourceType<R> type) {
    try {
      return state.findTombstone(id)
        .flatMap(r1 -> r1.as(type))
        .map(Maybe::just)
        .getOrElse(Maybe::empty);
    } catch (Exception e) {
      return Maybe.error(e);
    }
  }

  @Override
  public Observable<Link> linksFrom(UUID id) {
    return Observable.fromIterable(state.linksBySource(id));
  }

  @Override
  public Observable<Link> linksTo(UUID id) {
    return Observable.fromIterable(state.linksByTarget(id));
  }

  // ------------------------------------------------------------
  // Package-private

  <R extends Resource<R>> Observable<R> findChildrenOfType(Resource<?> parent, ResourceType<R> type) {
    var children = state.findChildrenOfType(parent.id(), type);
    return Observable.fromIterable(children);
  }

  Single<? extends Resource<?>> findParentOf(Resource<?> child) {
    return state.findParent(child).map(Single::just)
      .getOrElse(() -> Single.error(new NoSuchElementException("No parent found for resource: " + child)));
  }

  // ------------------------------------------------------------
  // Private

  private <P extends Resource<P>, C extends Resource<C>> Single<C> create(P parent, ResourceType<C> childType) {
    synchronized (mutex) {
      try {
        var result = state.createChild(this, parent, childType);
        state = result.stateNext();
        return just(result.resource());
      } catch (Exception e) {
        return Single.error(e);
      }
    }
  }

  private <R extends Resource<R>> Single<R> delete(R res, boolean recursive) {
    synchronized (mutex) {
      try {
        var result = recursive ? state.deleteRecursive(res) : state.delete(res);
        state = result.stateNext();
        return Single.just(result.resource());
      } catch (Exception e) {
        return Single.error(e);
      }
    }
  }
}
