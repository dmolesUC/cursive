package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

public interface CCollection {
  Option<CWorkspace> parentWorkspace();
  Option<CCollection> parentCollection();

  Traversable<CObject> memberObjects();
  Traversable<CCollection> memberCollections();
}
