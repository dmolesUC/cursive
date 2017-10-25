package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

public interface CObject extends Identified {
  Option<CObject> parentObject();
  Option<CCollection> parentCollection();

  Traversable<CFile> memberFiles();
  CFile createFile();

  Traversable<CObject> memberObjects();
  CObject createObject();

  Traversable<CObject> relatedObjects();

  /**
   * @throws NullPointerException if {@code toObject} is null
   * @throws IllegalArgumentException if {@code toObject} belongs to
   *   a different {@link Store} than this object
   */
  CRelation relateTo(CObject toObject);

  Traversable<CRelation> outgoingRelations();
  Traversable<CRelation> incomingRelations();
}
