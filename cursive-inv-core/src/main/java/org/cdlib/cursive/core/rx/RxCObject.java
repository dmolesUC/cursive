package org.cdlib.cursive.core.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vavr.control.Option;
import org.cdlib.cursive.core.CObject;

public interface RxCObject extends CObject {
  Maybe<RxCObject> parentObjectAsync();
  Maybe<RxCCollection> parentCollectionAsync();

  Observable<RxCFile> memberFilesAsync();
  Single<RxCFile> createFileAsync();

  Observable<RxCObject> memberObjectsAsync();
  Single<RxCObject> createObjectAsync();

  Observable<RxCObject> relatedObjectsAsync();
  Single<RxCRelation> relateTo(RxCObject toObject);
}
