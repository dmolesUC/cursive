package org.cdlib.cursive.pcdm;

import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;

public interface PcdmFile extends Resource {
  PcdmObject parentObject();

  @Override
  default ResourceType type() {
    return ResourceType.FILE;
  }

  @Override
  default String parentPath() {
    return parentObject().path();
  }
}
