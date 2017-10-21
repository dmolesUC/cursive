package org.cdlib.cursive.store;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.*;

public interface ReactiveStore {
  Observable<CWorkspace> workspaces();
  Single<CWorkspace> createWorkspace();

  Observable<CCollection> collections();
  Single<CCollection> createCollection();

  Observable<CObject> objects();
  Single<CObject> createObject();

  Observable<CFile> files();
  Observable<CRelation> relations();
}
