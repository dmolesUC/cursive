package org.cdlib.cursive.store;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CWorkspace;
import org.cdlib.cursive.core.CObject;

public interface Store {
  Traversable<CWorkspace> workspaces();
  Traversable<CCollection> collections();
  Traversable<CObject> objects();
  Traversable<CFile> files();
}
