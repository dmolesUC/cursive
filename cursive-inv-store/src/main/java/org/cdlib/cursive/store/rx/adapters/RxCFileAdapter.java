package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.rx.RxCFile;
import org.cdlib.cursive.core.rx.RxCObject;

class RxCFileAdapter extends RxResourceImpl<CFile> implements RxCFile {

  RxCFileAdapter(CFile file) {
    super(file);
  }

  @Override
  public Single<RxCObject> parentObject() {
    return Single.just(this.delegate.parentObject()).map(RxCObjectAdapter::new);
  }
}
