package org.cdlib.cursive.api.s11n;

import io.reactivex.Single;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;

public class ResultFactory {
  public Single<LinkedResult> toResult(AsyncPcdmFile file) {
    String path = "";
    return file.parentObject().map(parentObj ->
      new LinkedResult(path).withLink(Pcdm.FILE_OF, ""));
  }
}
