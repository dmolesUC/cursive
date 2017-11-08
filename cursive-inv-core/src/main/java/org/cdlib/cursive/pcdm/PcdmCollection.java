package org.cdlib.cursive.pcdm;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;

public interface PcdmCollection extends Resource {
  Option<Workspace> parentWorkspace();
  Option<PcdmCollection> parentCollection();

  Traversable<PcdmObject> memberObjects();
  PcdmObject createObject();

  Traversable<PcdmCollection> memberCollections();
  PcdmCollection createCollection();

  @Override
  default ResourceType type() {
    return ResourceType.COLLECTION;
  }
}
