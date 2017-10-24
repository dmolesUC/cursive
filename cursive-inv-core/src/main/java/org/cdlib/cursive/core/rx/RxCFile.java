package org.cdlib.cursive.core.rx;

import io.reactivex.Single;

public interface RxCFile   {
  Single<RxCObject> parentObject();
}
