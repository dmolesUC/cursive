package org.cdlib.cursive.store.rx;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.core.rx.*;

public interface RxStore {
  Observable<RxCWorkspace> workspacesAsync();
  Single<RxCWorkspace> createWorkspaceAsync();

  Observable<RxCCollection> collectionsAsync();
  Single<RxCCollection> createCollectionAsync();

  Observable<RxCObject> objectsAsync();
  Single<RxCObject> createObjectAsync();

  Observable<RxCFile> filesAsync();
  Observable<RxCRelation> relationsAsync();
}
