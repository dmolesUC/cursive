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
  Single<RxCRelation> relateTo(RxCObject toObject);

  Observable<RxCRelation> outgoingRelations();
  Observable<RxCRelation> incomingRelations();
}
