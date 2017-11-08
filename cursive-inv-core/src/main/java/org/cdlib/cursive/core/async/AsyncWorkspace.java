package org.cdlib.cursive.core.async;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;

public interface AsyncWorkspace extends AsyncResource {
  Observable<AsyncPcdmCollection> memberCollections();
  Single<AsyncPcdmCollection> createCollection();

  @Override
  default ResourceType type() {
    return ResourceType.WORKSPACE;
  }
}
