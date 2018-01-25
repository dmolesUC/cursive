package org.cdlib.cursive.pcdm;

import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.ResourceType;
import org.cdlib.cursive.core.Store;

import java.util.function.Function;

public interface PcdmObject extends Resource {
  Option<PcdmObject> parentObject();

  Option<PcdmCollection> parentCollection();

  Traversable<PcdmFile> memberFiles();

  PcdmFile createFile();

  Traversable<PcdmObject> memberObjects();

  PcdmObject createObject();

  Traversable<PcdmObject> relatedObjects();

  /**
   * @throws NullPointerException     if {@code toObject} is null
   * @throws IllegalArgumentException if {@code toObject} belongs to
   *                                  a different {@link Store} than this object
   */
  PcdmRelation relateTo(PcdmObject toObject);

  Traversable<PcdmRelation> outgoingRelations();

  Traversable<PcdmRelation> incomingRelations();

  @Override
  default ResourceType type() {
    return ResourceType.OBJECT;
  }

  @Override
  default String parentPath() {
    return parent().map(Resource::path).getOrElse("/");
  }

  default Option<Resource> parent() {
    return Stream.of(parentCollection(), parentObject())
      .flatMap(Option::<Resource>narrow)
      .headOption();
  }
}
