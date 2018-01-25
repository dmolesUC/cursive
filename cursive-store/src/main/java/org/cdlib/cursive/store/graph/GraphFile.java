package org.cdlib.cursive.store.graph;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

class GraphFile extends AbstractGraphResource implements PcdmFile {

  GraphFile(GraphStore store, Vertex vertex) {
    super(ResourceType.FILE, store, vertex);
  }

  @Override
  public PcdmObject parentObject() {
    return store().findFirstObject(parents()).getOrElseThrow(() -> new IllegalStateException("Can't find parent object for file " + id()));
  }
}
