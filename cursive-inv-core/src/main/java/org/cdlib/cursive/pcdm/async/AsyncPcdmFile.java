package org.cdlib.cursive.pcdm.async;

import io.reactivex.Single;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.async.AsyncResource;

public interface AsyncPcdmFile extends AsyncResource {
  Single<AsyncPcdmObject> parentObject();

  @Override
  default ResourceType type() {
    return ResourceType.FILE;
  }
}
