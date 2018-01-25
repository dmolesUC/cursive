package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.pcdm.*;

import java.util.UUID;

public interface Store {
  Traversable<Workspace> workspaces();
  Workspace createWorkspace();

  Traversable<PcdmCollection> allCollections();
  PcdmCollection createCollection();

  Traversable<PcdmObject> allObjects();
  PcdmObject createObject();

  Traversable<PcdmFile> allFiles();
  Traversable<PcdmRelation> allRelations();

  Option<Resource> find(UUID id);
}
