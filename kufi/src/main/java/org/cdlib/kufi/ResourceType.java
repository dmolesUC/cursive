package org.cdlib.kufi;

import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;

/**
 * Enumerated list of resource types. Should be an enum, but
 * until we get <a href="http://openjdk.java.net/jeps/301">JEP 301</a>
 * enhanced enums, a final class is the best we can do.
 *
 * @param <R>
 */
public final class ResourceType<R extends Resource<R>> {

  // ------------------------------------------------------------
  // Constants

  public static final ResourceType<Workspace> WORKSPACE = new ResourceType<>(Workspace.class);
  public static final ResourceType<Collection> COLLECTION = new ResourceType<>(Collection.class);

  // ------------------------------------------------------------
  // Fields

  private final Class<R> implType;

  // ------------------------------------------------------------
  // Constructor

  private ResourceType(Class<R> implType) {
    this.implType = implType;
    Registry.register(this);
  }

  // ------------------------------------------------------------
  // Public methods

  public Class<R> implType() {
    return implType;
  }

  @Override
  public String toString() {
    return implType().getSimpleName();
  }

  public R cast(Resource<?> resource) {
    if (!resource.hasType(this)) {
      throw new IllegalArgumentException(String.format("Expected %s, was %s", this, resource.type()));
    }
    return implType.cast(resource);
  }

  // ------------------------------------------------------------
  // Helper classes

  private static final class Registry {
    private static Map<Class<?>, ResourceType<?>> registry = LinkedHashMap.empty();

    private static void register(ResourceType<?> rt) {
      Class<?> implType = rt.implType();
      registry.get(implType).forEach(existing -> {
        var msg = String.format("A %s for %s already exists: %s", ResourceType.class.getSimpleName(), implType.getSimpleName(), existing);
        throw new IllegalStateException(msg);
      });
      registry = registry.put(implType, rt);
    }
  }
}
