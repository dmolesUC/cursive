package org.cdlib.kufi;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vavr.control.Either;

public interface Collection extends Resource<Collection> {
  Single<Either<Workspace, Collection>> parent();

  Observable<Collection> childCollections();
}
