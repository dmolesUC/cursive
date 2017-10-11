package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

public interface CObject {
  Option<CObject> parentObject();

  Traversable<CFile> memberFiles();
  Traversable<CObject> memberObjects();

  Traversable<CObject> relatedObjects();
}
