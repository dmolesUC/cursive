package org.cdlib.cursive.core.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CCollection;

public interface RxCCollection extends CCollection {
  Maybe<RxCWorkspace> parentWorkspaceAsync();
  Maybe<RxCCollection> parentCollectionAsync();

  Observable<RxCObject> memberObjectsAsync();
  Single<RxCObject> createObjectAsync();

  Observable<RxCCollection> memberCollectionsAsync();
  Single<RxCCollection> createCollectionAsync();
}
