package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.async.*;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.async.AsyncStore;
import org.cdlib.cursive.pcdm.async.*;
import org.cdlib.cursive.store.util.RxUtils;

import java.util.Objects;

public class AsyncStoreAdapter<S extends Store> implements AsyncStore {

  // ------------------------------
  // Fields

  private final S store;

  // ------------------------------
  // Constructors

  public AsyncStoreAdapter(S store) {
    Objects.requireNonNull(store);
    this.store = store;
  }

  // ------------------------------
  // AsyncStore

  @Override
  public Observable<AsyncWorkspace> workspaces() {
    return Observable.fromIterable(store.workspaces()).map(AsyncWorkspaceAdapter::new);
  }

  @Override
  public Single<AsyncWorkspace> createWorkspace() {
    return Single.just(store.createWorkspace()).map(AsyncWorkspaceAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmCollection> collections() {
    return Observable.fromIterable(store.collections()).map(AsyncPcdmCollectionAdapter::new);
  }

  @Override
  public Single<AsyncPcdmCollection> createCollection() {
    return Single.just(store.createCollection()).map(AsyncPcdmCollectionAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmObject> objects() {
    return Observable.fromIterable(store.objects()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Single<AsyncPcdmObject> createObject() {
    return Single.just(store.createObject()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmFile> files() {
    return Observable.fromIterable(store.files()).map(AsyncPcdmFileAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmRelation> relations() {
    return Observable.fromIterable(store.relations()).map(AsyncPcdmRelationAdapter::new);
  }

  @Override
  public Maybe<AsyncPcdmResource> find(String identifier) {
    return RxUtils.toMaybe(store.find(identifier)).map(AsyncPcdmResourceImpl::from);
  }
}
