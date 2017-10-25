package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.rx.RxCCollection;
import org.cdlib.cursive.core.rx.RxCFile;
import org.cdlib.cursive.core.rx.RxCObject;
import org.cdlib.cursive.core.rx.RxCRelation;
import org.cdlib.cursive.store.util.RxUtils;

import java.util.Objects;

class RxCObjectAdapter implements RxCObject {
  private final CObject object;

  RxCObjectAdapter(CObject object) {
    Objects.requireNonNull(object);
    this.object = object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RxCObjectAdapter that = (RxCObjectAdapter) o;
    return object.equals(that.object);
  }

  @Override
  public int hashCode() {
    return object.hashCode();
  }

  @Override
  public Maybe<RxCObject> parentObject() {
    return RxUtils.toMaybe(object.parentObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Maybe<RxCCollection> parentCollection() {
    return RxUtils.toMaybe(object.parentCollection()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Observable<RxCFile> memberFiles() {
    return Observable.fromIterable(object.memberFiles()).map(RxCFileAdapter::new);
  }

  @Override
  public Single<RxCFile> createFile() {
    return Single.just(object.createFile()).map(RxCFileAdapter::new);
  }

  @Override
  public Observable<RxCObject> memberObjects() {
    return Observable.fromIterable(object.memberObjects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCObject> createObject() {
    return Single.just(object.createObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Observable<RxCObject> relatedObjects() {
    return Observable.fromIterable(object.relatedObjects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCRelation> relateTo(RxCObject toObject) {
    Objects.requireNonNull(toObject);
    if (!(toObject instanceof RxCObjectAdapter)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }
    return Single.just(object.relateTo(((RxCObjectAdapter) toObject).object)).map(RxCRelationAdapter::new);
  }

  @Override
  public Observable<RxCRelation> outgoingRelations() {
    return Observable.fromIterable(object.outgoingRelations()).map(RxCRelationAdapter::new);
  }

  @Override
  public Observable<RxCRelation> incomingRelations() {
    return Observable.fromIterable(object.incomingRelations()).map(RxCRelationAdapter::new);
  }
}
