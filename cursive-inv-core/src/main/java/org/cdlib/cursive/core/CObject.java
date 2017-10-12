package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

public interface CObject {
  Option<CObject> parentObject();
  Option<CCollection> parentCollection();

  Traversable<CFile> memberFiles();

  Traversable<CObject> memberObjects();
  CObject createObject();

  Traversable<CObject> relatedObjects();
}
