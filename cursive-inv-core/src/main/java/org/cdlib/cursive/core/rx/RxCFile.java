package org.cdlib.cursive.core.rx;

import io.reactivex.Single;

public interface RxCFile extends RxResource {
  Single<RxCObject> parentObject();
}
