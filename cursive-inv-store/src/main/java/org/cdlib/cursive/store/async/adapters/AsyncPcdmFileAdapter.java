package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;

class AsyncPcdmFileAdapter extends AsyncPcdmResourceImpl<PcdmFile> implements AsyncPcdmFile {

  AsyncPcdmFileAdapter(PcdmFile file) {
    super(file);
  }

  @Override
  public Single<AsyncPcdmObject> parentObject() {
    return Single.just(this.delegate.parentObject()).map(AsyncPcdmObjectAdapter::new);
  }
}
