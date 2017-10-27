package org.cdlib.cursive.core;

import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.*;

public interface Store {
  Traversable<CWorkspace> workspaces();
  CWorkspace createWorkspace();

  Traversable<CCollection> collections();
  CCollection createCollection();

  Traversable<CObject> objects();
  CObject createObject();

  Traversable<CFile> files();
  Traversable<CRelation> relations();

  Option<Resource> find(String identifier);
}
