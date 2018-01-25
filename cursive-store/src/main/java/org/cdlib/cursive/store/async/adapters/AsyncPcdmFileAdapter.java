package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;

class AsyncPcdmFileAdapter extends AsyncResourceImpl<PcdmFile> implements AsyncPcdmFile {

  AsyncPcdmFileAdapter(PcdmFile file) {
    super(file);
  }

  @Override
  public Single<AsyncPcdmObject> parentObject() {
    return Single.just(delegate.parentObject()).map(AsyncPcdmObjectAdapter::new);
  }
}
