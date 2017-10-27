package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.core.CRelation;
import org.cdlib.cursive.core.rx.RxCObject;
import org.cdlib.cursive.core.rx.RxCRelation;

import java.util.Objects;

class RxCRelationAdapter implements RxCRelation {
  private final CRelation delegate;

  RxCRelationAdapter(CRelation delegate) {
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
    RxCRelationAdapter that = (RxCRelationAdapter) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public Single<RxCObject> fromObject() {
    return Single.just(delegate.fromObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> toObject() {
    return Single.just(delegate.toObject()).map(RxCObjectAdapter::new);
  }
}
