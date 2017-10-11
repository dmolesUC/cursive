package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;

public interface CWorkspace {
  Traversable<CCollection> memberCollections();
}
