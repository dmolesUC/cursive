package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.rx.RxCCollection;
import org.cdlib.cursive.core.rx.RxCObject;
import org.cdlib.cursive.core.rx.RxCWorkspace;
import org.cdlib.cursive.store.util.RxUtils;

class RxCCollectionAdapter extends RxResourceImpl<CCollection> implements RxCCollection {
  RxCCollectionAdapter(CCollection collection) {
    super(collection);
  }

  @Override
  public Maybe<RxCWorkspace> parentWorkspace() {
    return RxUtils.toMaybe(delegate.parentWorkspace()).map(RxCWorkspaceAdapter::new);
  }

  @Override
  public Maybe<RxCCollection> parentCollection() {
    return RxUtils.toMaybe(delegate.parentCollection()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Observable<RxCObject> memberObjects() {
    return Observable.fromIterable(delegate.memberObjects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> createObject() {
    return Single.just(delegate.createObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Observable<RxCCollection> memberCollections() {
    return Observable.fromIterable(delegate.memberCollections()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Single<RxCCollection> createCollection() {
    return Single.just(delegate.createCollection()).map(RxCCollectionAdapter::new);
  }
}
