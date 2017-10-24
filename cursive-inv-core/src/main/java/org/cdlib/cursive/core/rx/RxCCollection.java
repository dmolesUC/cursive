package org.cdlib.cursive.core.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface RxCCollection   {
  Maybe<RxCWorkspace> parentWorkspace();
  Maybe<RxCCollection> parentCollection();

  Observable<RxCObject> memberObjects();
  Single<RxCObject> createObject();

  Observable<RxCCollection> memberCollections();
  Single<RxCCollection> createCollection();
}
