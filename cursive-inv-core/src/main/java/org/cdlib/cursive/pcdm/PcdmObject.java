package org.cdlib.cursive.pcdm;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Store;

public interface PcdmObject extends PcdmResource {
  Option<PcdmObject> parentObject();
  Option<PcdmCollection> parentCollection();

  Traversable<PcdmFile> memberFiles();
  PcdmFile createFile();

  Traversable<PcdmObject> memberObjects();
  PcdmObject createObject();

  Traversable<PcdmObject> relatedObjects();

  /**
   * @throws NullPointerException if {@code toObject} is null
   * @throws IllegalArgumentException if {@code toObject} belongs to
   *   a different {@link Store} than this object
   */
  PcdmRelation relateTo(PcdmObject toObject);

  Traversable<PcdmRelation> outgoingRelations();
  Traversable<PcdmRelation> incomingRelations();
}
