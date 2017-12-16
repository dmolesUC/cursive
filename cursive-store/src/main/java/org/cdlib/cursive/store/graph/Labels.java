package org.cdlib.cursive.store.graph;

import io.vavr.Tuple;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.cdlib.cursive.core.ResourceType;

class Labels {

  static String STORE = "STORE";
  static String PARENT_CHILD = "PARENT_CHILD";

  private static Map<String, ResourceType> directory = Stream.of(ResourceType.values()).toMap(t -> Tuple.of(labelFor(t), t));

  public static String labelFor(ResourceType resourceType) {
    return resourceType.name();
  }

  public static Option<ResourceType> resourceTypeOf(String label) {
    return directory.get(label);
  }

  private Labels() {
    // private to prevent instantiation
  }
}
