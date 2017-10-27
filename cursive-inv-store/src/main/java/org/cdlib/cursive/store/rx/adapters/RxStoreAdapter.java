package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.rx.*;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.rx.RxStore;
import org.cdlib.cursive.store.util.RxUtils;

import java.util.Objects;

public class RxStoreAdapter<S extends Store> implements RxStore {

  // ------------------------------
  // Fields

  private final S store;

  // ------------------------------
  // Constructors

  public RxStoreAdapter(S store) {
    Objects.requireNonNull(store);
    this.store = store;
  }

  // ------------------------------
  // RxStore

  @Override
  public Observable<RxCWorkspace> workspaces() {
    return Observable.fromIterable(store.workspaces()).map(RxCWorkspaceAdapter::new);
  }

  @Override
  public Single<RxCWorkspace> createWorkspace() {
    return Single.just(store.createWorkspace()).map(RxCWorkspaceAdapter::new);
  }

  @Override
  public Observable<RxCCollection> collections() {
    return Observable.fromIterable(store.collections()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Single<RxCCollection> createCollection() {
    return Single.just(store.createCollection()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Observable<RxCObject> objects() {
    return Observable.fromIterable(store.objects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> createObject() {
    return Single.just(store.createObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Observable<RxCFile> files() {
    return Observable.fromIterable(store.files()).map(RxCFileAdapter::new);
  }

  @Override
  public Observable<RxCRelation> relations() {
    return Observable.fromIterable(store.relations()).map(RxCRelationAdapter::new);
  }

  @Override
  public Maybe<RxResource> find(String identifier) {
    return RxUtils.toMaybe(store.find(identifier)).map(RxResourceImpl::from);
  }
}
