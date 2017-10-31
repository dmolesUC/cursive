package org.cdlib.cursive.pcdm.async;

import io.reactivex.Single;

public interface AsyncPcdmRelation {
  Single<AsyncPcdmObject> fromObject();
  Single<AsyncPcdmObject> toObject();
}
