package org.cdlib.cursive.core.rx;

import io.reactivex.Single;
import org.cdlib.cursive.core.CFile;

public interface RxCFile extends CFile {
  Single<RxCObject> parentObjectAsync();
}
