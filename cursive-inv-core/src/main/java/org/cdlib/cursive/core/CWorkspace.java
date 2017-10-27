package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;

public interface CWorkspace extends Resource {
  Traversable<CCollection> memberCollections();
  CCollection createCollection();
}
