package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.pcdm.*;

public interface Store {
  Traversable<Workspace> workspaces();
  Workspace createWorkspace();

  Traversable<PcdmCollection> collections();
  PcdmCollection createCollection();

  Traversable<PcdmObject> objects();
  PcdmObject createObject();

  Traversable<PcdmFile> files();
  Traversable<PcdmRelation> relations();

  Option<Resource> find(String identifier);
}
