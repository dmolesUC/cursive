package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.core.CRelation;
import org.cdlib.cursive.core.rx.RxCObject;
import org.cdlib.cursive.core.rx.RxCRelation;

import java.util.Objects;

class RxCRelationAdapter implements RxCRelation {
  private final CRelation relation;

  RxCRelationAdapter(CRelation relation) {
    Objects.requireNonNull(relation);
    this.relation = relation;
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
    return relation.equals(that.relation);
  }

  @Override
  public int hashCode() {
    return relation.hashCode();
  }

  @Override
  public Single<RxCObject> fromObject() {
    return Single.just(relation.fromObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> toObject() {
    return Single.just(relation.toObject()).map(RxCObjectAdapter::new);
  }
}
