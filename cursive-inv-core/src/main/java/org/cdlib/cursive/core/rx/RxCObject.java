package org.cdlib.cursive.core.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface RxCObject   {
  Maybe<RxCObject> parentObject();
  Maybe<RxCCollection> parentCollection();

  Observable<RxCFile> memberFiles();
  Single<RxCFile> createFile();

  Observable<RxCObject> memberObjects();
  Single<RxCObject> createObject();

  Observable<RxCObject> relatedObjects();

  /**
   * @throws NullPointerException if {@code toObject} is null
   * @throws IllegalArgumentException if {@code toObject} belongs to
   *   a different {@link RxStore} than this object
   */
  Single<RxCRelation> relateTo(RxCObject toObject);

  Observable<RxCRelation> outgoingRelations();
  Observable<RxCRelation> incomingRelations();
}
