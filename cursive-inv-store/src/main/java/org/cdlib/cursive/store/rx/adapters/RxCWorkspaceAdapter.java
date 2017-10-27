package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CWorkspace;
import org.cdlib.cursive.core.rx.RxCCollection;
import org.cdlib.cursive.core.rx.RxCWorkspace;

class RxCWorkspaceAdapter extends RxResourceImpl<CWorkspace> implements RxCWorkspace {

  RxCWorkspaceAdapter(CWorkspace workspace) {
    super(workspace);
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
