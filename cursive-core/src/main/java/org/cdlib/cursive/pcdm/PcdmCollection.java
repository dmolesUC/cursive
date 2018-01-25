package org.cdlib.cursive.pcdm;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Workspace;

import java.util.function.Function;

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

  @Override
  default String parentPath() {
    return parent().map(Resource::path).getOrElse("/");
  }

  default Option<Resource> parent() {
    return Stream.of(parentWorkspace(), parentCollection())
      .flatMap(Option::<Resource>narrow)
      .headOption();
  }
}
