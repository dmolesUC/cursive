package org.cdlib.cursive.core.rx;

import io.reactivex.Single;

public interface RxCRelation   {
  Single<RxCObject> fromObject();
  Single<RxCObject> toObject();
}
