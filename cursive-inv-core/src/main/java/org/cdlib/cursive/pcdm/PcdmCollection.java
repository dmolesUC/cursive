package org.cdlib.cursive.pcdm;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Workspace;

public interface PcdmCollection extends PcdmResource {
  Option<Workspace> parentWorkspace();
  Option<PcdmCollection> parentCollection();

  Traversable<PcdmObject> memberObjects();
  PcdmObject createObject();

  Traversable<PcdmCollection> memberCollections();
  PcdmCollection createCollection();
}
