package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.rx.RxCCollection;
import org.cdlib.cursive.core.rx.RxCObject;
import org.cdlib.cursive.core.rx.RxCWorkspace;
import org.cdlib.cursive.store.util.RxUtils;

import java.util.Objects;

class RxCCollectionAdapter implements RxCCollection {
  private final CCollection collection;

  RxCCollectionAdapter(CCollection collection) {
    Objects.requireNonNull(collection);
    this.collection = collection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RxCCollectionAdapter that = (RxCCollectionAdapter) o;
    return collection.equals(that.collection);
  }

  @Override
  public int hashCode() {
    return collection.hashCode();
  }

  @Override
  public Maybe<RxCWorkspace> parentWorkspace() {
    return RxUtils.toMaybe(collection.parentWorkspace()).map(RxCWorkspaceAdapter::new);
  }

  @Override
  public Maybe<RxCCollection> parentCollection() {
    return RxUtils.toMaybe(collection.parentCollection()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Observable<RxCObject> memberObjects() {
    return Observable.fromIterable(collection.memberObjects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> createObject() {
    return Single.just(collection.createObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Observable<RxCCollection> memberCollections() {
    return Observable.fromIterable(collection.memberCollections()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Single<RxCCollection> createCollection() {
    return Single.just(collection.createCollection()).map(RxCCollectionAdapter::new);
  }
}
