package org.cdlib.cursive.pcdm.async;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.async.AsyncResource;
import org.cdlib.cursive.core.async.AsyncWorkspace;

public interface AsyncPcdmCollection extends AsyncResource {
  Maybe<AsyncWorkspace> parentWorkspace();

  Maybe<AsyncPcdmCollection> parentCollection();

  Observable<AsyncPcdmObject> memberObjects();

  Single<AsyncPcdmObject> createObject();

  Observable<AsyncPcdmCollection> memberCollections();

  Single<AsyncPcdmCollection> createCollection();

  @Override
  default ResourceType type() {
    return ResourceType.COLLECTION;
  }
}
