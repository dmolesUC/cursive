package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.pcdm.async.AsyncPcdmRelation;
import org.cdlib.cursive.store.util.RxUtils;

import java.util.Objects;

class AsyncPcdmObjectAdapter extends AsyncPcdmResourceImpl<PcdmObject> implements AsyncPcdmObject {

  AsyncPcdmObjectAdapter(PcdmObject object) {
    super(object);
  }

  @Override
  public Maybe<AsyncPcdmObject> parentObject() {
    return RxUtils.toMaybe(delegate.parentObject()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Maybe<AsyncPcdmCollection> parentCollection() {
    return RxUtils.toMaybe(delegate.parentCollection()).map(AsyncPcdmCollectionAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmFile> memberFiles() {
    return Observable.fromIterable(delegate.memberFiles()).map(AsyncPcdmFileAdapter::new);
  }

  @Override
  public Single<AsyncPcdmFile> createFile() {
    return Single.just(delegate.createFile()).map(AsyncPcdmFileAdapter::new);
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
  public Observable<AsyncPcdmObject> relatedObjects() {
    return Observable.fromIterable(delegate.relatedObjects()).map(AsyncPcdmObjectAdapter::new);
  }

  @Override
  public Single<AsyncPcdmRelation> relateTo(AsyncPcdmObject toObject) {
    Objects.requireNonNull(toObject);
    if (!(toObject instanceof AsyncPcdmObjectAdapter)) {
      throw new IllegalArgumentException(String.format("Related object <%s> must be from the same store as <%s>", toObject, this));
    }
    return Single.just(delegate.relateTo(((AsyncPcdmObjectAdapter) toObject).delegate)).map(AsyncPcdmRelationAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmRelation> outgoingRelations() {
    return Observable.fromIterable(delegate.outgoingRelations()).map(AsyncPcdmRelationAdapter::new);
  }

  @Override
  public Observable<AsyncPcdmRelation> incomingRelations() {
    return Observable.fromIterable(delegate.incomingRelations()).map(AsyncPcdmRelationAdapter::new);
  }
}
