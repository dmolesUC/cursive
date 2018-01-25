package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.core.async.AsyncWorkspace;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;

class AsyncWorkspaceAdapter extends AsyncResourceImpl<Workspace> implements AsyncWorkspace {

  AsyncWorkspaceAdapter(Workspace workspace) {
    super(workspace);
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
