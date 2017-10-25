package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CWorkspace;
import org.cdlib.cursive.core.rx.RxCCollection;
import org.cdlib.cursive.core.rx.RxCWorkspace;

import java.util.Objects;

class RxCWorkspaceAdapter implements RxCWorkspace {
  private final CWorkspace workspace;

  RxCWorkspaceAdapter(CWorkspace workspace) {
    Objects.requireNonNull(workspace);
    this.workspace = workspace;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RxCWorkspaceAdapter that = (RxCWorkspaceAdapter) o;
    return workspace.equals(that.workspace);
  }

  @Override
  public int hashCode() {
    return workspace.hashCode();
  }

  @Override
  public Observable<RxCCollection> memberCollections() {
    return Observable.fromIterable(workspace.memberCollections()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Single<RxCCollection> createCollection() {
    return Single.just(workspace.createCollection()).map(RxCCollectionAdapter::new);
  }
}
