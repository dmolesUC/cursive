package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.pcdm.PcdmRelation;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.pcdm.async.AsyncPcdmRelation;

import java.util.Objects;

class AsyncPcdmRelationAdapter implements AsyncPcdmRelation {
  private final PcdmRelation delegate;

  AsyncPcdmRelationAdapter(PcdmRelation delegate) {
    Objects.requireNonNull(delegate);
    this.delegate = delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AsyncPcdmRelationAdapter that = (AsyncPcdmRelationAdapter) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public Single<AsyncPcdmObject> fromObject() {
    return Single.just(delegate.fromObject()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Single<AsyncPcdmObject> toObject() {
    return Single.just(delegate.toObject()).map(AsyncPcdmObjectAdapter::new);
  }
}
