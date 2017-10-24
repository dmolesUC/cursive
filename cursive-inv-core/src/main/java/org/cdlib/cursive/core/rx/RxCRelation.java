package org.cdlib.cursive.core.rx;

import io.reactivex.Single;
import org.cdlib.cursive.core.CRelation;

public interface RxCRelation extends CRelation {
  Single<RxCObject> fromObjectAsync();
  Single<RxCObject> toObjectAsync();
}
