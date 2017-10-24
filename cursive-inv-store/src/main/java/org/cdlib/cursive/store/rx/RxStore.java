package org.cdlib.cursive.store.rx;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.rx.*;

public interface RxStore {
  Observable<RxCWorkspace> workspaces();
  Single<RxCWorkspace> createWorkspace();

  Observable<RxCCollection> collections();
  Single<RxCCollection> createCollection();

  Observable<RxCObject> objects();
  Single<RxCObject> createObject();

  Observable<RxCFile> files();
  Observable<RxCRelation> relations();
}
