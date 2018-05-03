package org.cdlib.kufi;

import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.Objects;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

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

  public Option<R> cast(Resource<?> resource) {
    if (is(resource)) {
      return some(implType.cast(resource));
    }
    return none();
  }

  // TODO: move to Resource
  public R as(Resource<?> resource) {
    if (!is(resource)) {
      throw new IllegalArgumentException(String.format("Expected %s, was %s", this, resource.type()));
    }
    return implType.cast(resource);
  }

  // TODO: move to Resource
  public boolean is(Resource<?> resource) {
    Objects.requireNonNull(resource);
    return resource.type() == this;
  }

  @Override
  public String toString() {
    return implType.getSimpleName();
  }

  // ------------------------------------------------------------
  // Helper classes

  private static final class Registry {
    private static Map<Class<?>, ResourceType<?>> registry = LinkedHashMap.empty();

    private static void register(ResourceType<?> rt) {
      Class<?> implType = rt.implType;
      registry.get(implType).forEach(existing -> {
        var msg = String.format("A %s for %s already exists: %s", ResourceType.class.getSimpleName(), implType.getSimpleName(), existing);
        throw new IllegalStateException(msg);
      });
      registry = registry.put(implType, rt);
    }
  }
}
