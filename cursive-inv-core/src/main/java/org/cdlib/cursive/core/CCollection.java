package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;

public interface CCollection extends Identified {
  Option<CWorkspace> parentWorkspace();
  Option<CCollection> parentCollection();

  Traversable<CObject> memberObjects();
  CObject createObject();

  Traversable<CCollection> memberCollections();
  CCollection createCollection();
}
