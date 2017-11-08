package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.pcdm.PcdmCollection;

public interface Workspace extends Resource {
  Traversable<PcdmCollection> memberCollections();
  PcdmCollection createCollection();

  @Override
  default ResourceType type() {
    return ResourceType.WORKSPACE;
  }
}
