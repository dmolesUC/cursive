package org.cdlib.cursive.core.async;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.pcdm.async.AsyncPcdmRelation;

import java.util.UUID;

public interface AsyncStore {
  Observable<AsyncWorkspace> workspaces();

  Single<AsyncWorkspace> createWorkspace();

  Observable<AsyncPcdmCollection> collections();

  Single<AsyncPcdmCollection> createCollection();

  Observable<AsyncPcdmObject> objects();

  Single<AsyncPcdmObject> createObject();

  Observable<AsyncPcdmFile> files();

  Observable<AsyncPcdmRelation> relations();

  Maybe<AsyncResource> find(UUID identifier);
}
