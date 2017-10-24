package org.cdlib.cursive.core.rx;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface RxCWorkspace   {
  Observable<RxCCollection> memberCollections();
  Single<RxCCollection> createCollection();
}
