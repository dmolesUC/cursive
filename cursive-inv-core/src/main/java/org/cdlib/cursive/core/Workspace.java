package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmResource;

public interface Workspace extends PcdmResource {
  Traversable<PcdmCollection> memberCollections();
  PcdmCollection createCollection();
}
