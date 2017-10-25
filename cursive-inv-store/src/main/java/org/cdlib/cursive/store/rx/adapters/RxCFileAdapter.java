package org.cdlib.cursive.store.rx.adapters;

import io.reactivex.Single;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.rx.RxCFile;
import org.cdlib.cursive.core.rx.RxCObject;

import java.util.Objects;

class RxCFileAdapter implements RxCFile {
  private final CFile file;

  RxCFileAdapter(CFile file) {
    Objects.requireNonNull(file);
    this.file = file;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RxCFileAdapter that = (RxCFileAdapter) o;
    return file.equals(that.file);
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  @Override
  public Single<RxCObject> parentObject() {
    return Single.just(file.parentObject()).map(RxCObjectAdapter::new);
  }
}
