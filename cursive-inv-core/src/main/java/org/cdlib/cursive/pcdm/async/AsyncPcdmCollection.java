package org.cdlib.cursive.pcdm.async;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.async.AsyncWorkspace;

public interface AsyncPcdmCollection extends AsyncPcdmResource {
  Maybe<AsyncWorkspace> parentWorkspace();
  Maybe<AsyncPcdmCollection> parentCollection();

  Observable<AsyncPcdmObject> memberObjects();
  Single<AsyncPcdmObject> createObject();

  Observable<AsyncPcdmCollection> memberCollections();
  Single<AsyncPcdmCollection> createCollection();
}
