package org.cdlib.kufi.memory;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.cdlib.kufi.Collection;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Workspace;

class Constructors {
  private static final Map<ResourceType<?>, ResourceConstructor<?>> creators = HashMap.of(
    ResourceType.WORKSPACE, (ResourceConstructor<Workspace>) MemoryWorkspace::new,
    ResourceType.COLLECTION, (ResourceConstructor<Collection>) MemoryCollection::new
  );

  @SuppressWarnings("unchecked")
  static <R extends Resource<R>> ResourceConstructor<R> creatorFor(ResourceType<R> type) {
    return creators.get(type).map(b -> (ResourceConstructor<R>) b).get();
  }

}
