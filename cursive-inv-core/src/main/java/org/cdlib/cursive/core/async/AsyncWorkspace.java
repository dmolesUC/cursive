package org.cdlib.cursive.core.async;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmResource;

public interface AsyncWorkspace extends AsyncPcdmResource {
  Observable<AsyncPcdmCollection> memberCollections();
  Single<AsyncPcdmCollection> createCollection();
}
