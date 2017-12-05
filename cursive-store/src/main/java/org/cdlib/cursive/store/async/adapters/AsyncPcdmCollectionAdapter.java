package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.core.async.AsyncWorkspace;
import org.cdlib.cursive.store.util.RxUtils;

class AsyncPcdmCollectionAdapter extends AsyncResourceImpl<PcdmCollection> implements AsyncPcdmCollection {
  AsyncPcdmCollectionAdapter(PcdmCollection collection) {
    super(collection);
  }

  @Override
  public Maybe<AsyncWorkspace> parentWorkspace() {
    return RxUtils.toMaybe(delegate.parentWorkspace()).map(AsyncWorkspaceAdapter::new);
  }

  @Override
  public Maybe<AsyncPcdmCollection> parentCollection() {
    return RxUtils.toMaybe(delegate.parentCollection()).map(AsyncPcdmCollectionAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmObject> memberObjects() {
    return Observable.fromIterable(delegate.memberObjects()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Single<AsyncPcdmObject> createObject() {
    return Single.just(delegate.createObject()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmCollection> memberCollections() {
    return Observable.fromIterable(delegate.memberCollections()).map(AsyncPcdmCollectionAdapter::new);
  }

  @Override
  public Single<AsyncPcdmCollection> createCollection() {
    return Single.just(delegate.createCollection()).map(AsyncPcdmCollectionAdapter::new);
  }
}
