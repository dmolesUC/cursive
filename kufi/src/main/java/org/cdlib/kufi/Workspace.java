package org.cdlib.kufi;

import io.reactivex.Observable;

public interface Workspace extends Resource<Workspace> {
  Observable<Collection> childCollections();
}
