package org.cdlib.cursive.pcdm.async;

import io.reactivex.Single;

public interface AsyncPcdmFile extends AsyncPcdmResource {
  Single<AsyncPcdmObject> parentObject();
}
