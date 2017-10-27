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

class RxCObjectAdapter extends RxResourceImpl<CObject> implements RxCObject {

  RxCObjectAdapter(CObject object) {
    super(object);
  }

  @Override
  public Maybe<RxCObject> parentObject() {
    return RxUtils.toMaybe(delegate.parentObject()).map(RxCObjectAdapter::new);
  }

  @Override
  public Maybe<RxCCollection> parentCollection() {
    return RxUtils.toMaybe(delegate.parentCollection()).map(RxCCollectionAdapter::new);
  }

  @Override
  public Observable<RxCFile> memberFiles() {
    return Observable.fromIterable(delegate.memberFiles()).map(RxCFileAdapter::new);
  }

  @Override
  public Single<RxCFile> createFile() {
    return Single.just(delegate.createFile()).map(RxCFileAdapter::new);
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
  public Observable<RxCObject> relatedObjects() {
    return Observable.fromIterable(delegate.relatedObjects()).map(RxCObjectAdapter::new);
  }

  @Override
  public Single<RxCRelation> relateTo(RxCObject toObject) {
    Objects.requireNonNull(toObject);
    if (!(toObject instanceof RxCObjectAdapter)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }
    return Single.just(delegate.relateTo(((RxCObjectAdapter) toObject).delegate)).map(RxCRelationAdapter::new);
  }

  @Override
  public Observable<RxCRelation> outgoingRelations() {
    return Observable.fromIterable(delegate.outgoingRelations()).map(RxCRelationAdapter::new);
  }

  @Override
  public Observable<RxCRelation> incomingRelations() {
    return Observable.fromIterable(delegate.incomingRelations()).map(RxCRelationAdapter::new);
  }
}
