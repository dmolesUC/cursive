package org.cdlib.cursive.store.graph;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;

import static org.cdlib.cursive.store.graph.GraphResourceUtils.findFirstObject;

class GraphFile extends AbstractGraphResource implements PcdmFile {

  GraphFile(Vertex vertex) {
    super(ResourceType.FILE, vertex);
  }

  @Override
  public PcdmObject parentObject() {
    return findFirstObject(parents()).getOrElseThrow(() -> new IllegalStateException("Can't find parent object for file " + identifier()));
  }
}
