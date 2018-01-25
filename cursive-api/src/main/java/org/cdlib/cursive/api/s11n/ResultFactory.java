package org.cdlib.cursive.api.s11n;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;

class ResultFactory {
  public Single<LinkedResult> toResult(AsyncPcdmFile file) {
    return file.parentObject().map(parentObj ->
      new LinkedResult(file.path()).withLink(Pcdm.FILE_OF, parentObj.path()));
  }

  @SuppressWarnings("unchecked")
  public Single<LinkedResult> toResult(AsyncPcdmObject object) {
    return Observable.ambArray(
      object.parent().map(p -> new Link(Pcdm.MEMBER_OF, p.path())).toObservable(),
      // TODO: figure out why this doesn't work
      object.memberFiles().map(f -> new Link(Pcdm.HAS_FILE, f.path())),
      // TODO: figure out why this doesn't work
      object.memberObjects().map(o -> new Link(Pcdm.HAS_MEMBER, o.path())),
      object.relatedObjects().map(o1 -> new Link(Pcdm.HAS_RELATED_OBJECT, o1.path())),
      object.incomingRelations().flatMap(r -> r.fromObject().toObservable()).map(o2 -> new Link(Pcdm.RELATED_OBJECT_OF, o2.path()))
    ).reduce(
      new LinkedResult(object.path()),
      (res0, link) -> {
        LinkedResult res1 = res0.withLink(link);
//        System.out.println("res0:\t" + res0);
//        System.out.println("res1:\t" + res1);
        return res1;
      }
    );
  }
}
