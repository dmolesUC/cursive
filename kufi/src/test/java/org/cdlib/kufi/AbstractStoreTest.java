package org.cdlib.kufi;

import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.cdlib.cursive.util.RxAssertions.assertThat;
import static org.cdlib.cursive.util.RxAssertions.valueEmittedBy;
import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

public abstract class AbstractStoreTest<S extends Store> {

  // ------------------------------------------------------------
  // Abstracts

  protected abstract S newStore();

  // ------------------------------------------------------------
  // Fixture

  private S store;

  // TODO: test dead links

  @BeforeEach
  void setUp() {
    store = newStore();
  }

  private Resource<?> createParent(ResourceType<?> parentType) {
    var rootWorkspace = valueEmittedBy(store.createWorkspace());
    if (WORKSPACE == parentType) {
      return rootWorkspace;
    } else if (COLLECTION == parentType) {
      return valueEmittedBy(store.createCollection(rootWorkspace));
    } else {
      throw new UnsupportedOperationException("Unknown resource type: " + parentType);
    }
  }

  private Single<? extends Resource<?>> create(Resource<?> parent, ResourceType<?> childType) {
    var createMethodName = "create" + childType;
    Class<?> implType = parent.type().implType();
    try {
      var m = Store.class.getDeclaredMethod(createMethodName, implType);
      @SuppressWarnings("unchecked")
      var result = (Single<? extends Resource<?>>) m.invoke(store, parent);
      return result;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  private Single<? extends Resource<?>> delete(Resource<?> resource) {
    var resourceType = resource.type();
    var deleteMethodName = "delete" + resourceType;
    Class<?> implType = resourceType.implType();
    try {
      var m = Store.class.getDeclaredMethod(deleteMethodName, implType);
      @SuppressWarnings("unchecked")
      var result = (Single<? extends Resource<?>>) m.invoke(store, resource);
      return result;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  private Single<? extends Resource<?>> deleteRecursive(Resource<?> resource) {
    var resourceType = resource.type();
    var deleteMethodName = "delete" + resourceType;
    Class<?> implType = resourceType.implType();
    try {
      var m = Store.class.getDeclaredMethod(deleteMethodName, implType, boolean.class);
      @SuppressWarnings("unchecked")
      var result = (Single<? extends Resource<?>>) m.invoke(store, resource, true);
      return result;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unused")
  static Stream<Arguments> parentToChildTypes() {
    return ResourceType.values().flatMap(p -> p.allowableChildren().map(c -> Arguments.of(p, c))).toJavaStream();
  }

  // ------------------------------------------------------------
  // Tests

  @Nested
  @SuppressWarnings("JUnit5MalformedParameterized")
  class Relations {
    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void createChild(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);

      var child = valueEmittedBy(create(parent, childType));
      assertThat(child.hasType(childType)).isTrue();
      var tx = child.currentVersion().transaction();

      var parentNext = valueEmittedBy(store.find(parent.id(), parentType));
      assertThat(parentNext.isLaterVersionOf(parent)).isTrue();
      assertThat(parentNext.currentVersion().transaction()).isEqualTo(tx);

      assertThat(store.transaction()).emitted(tx);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void createChildFailsWithTombstonedParent(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);
      var tombstone = valueEmittedBy(delete(parent));
      var tx = valueEmittedBy(store.transaction());
      assertThat(tombstone.deletedAtTransaction()).contains(tx);

      assertThat(create(parent, childType)).emittedOneError();
      assertThat(store.findTombstone(parent.id())).emitted(tombstone);
      assertThat(store.transaction()).emitted(tx);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void deleteParentFailsWithChild(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);
      var child = valueEmittedBy(create(parent, childType));
      var parentNext = valueEmittedBy(store.find(parent.id(), parentType));
      var tx = valueEmittedBy(store.transaction());

      assertThat(delete(parent)).emittedOneError();

      assertThat(valueEmittedBy(store.find(parent.id(), parentType))).isEqualTo(parentNext);
      assertThat(valueEmittedBy(store.find(child.id(), childType))).isEqualTo(child);
      assertThat(store.transaction()).emitted(tx);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void deleteParentRecursiveDeletesChild(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);
      var child = valueEmittedBy(create(parent, childType));
      var tx = valueEmittedBy(store.transaction());

      assertThat(deleteRecursive(parent)).emittedValueThat(isTombstoneFor(parent));

      assertThat(store.findTombstone(parent.id())).emittedValueThat(isTombstoneFor(parent));
      assertThat(store.findTombstone(child.id())).emittedValueThat(isTombstoneFor(child));

      assertThat(store.transaction()).emittedValueThat(tx::lessThan);
    }
  }

  @Nested
  class Workspaces {
    @Test
    void createWorkspaceIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction()).txid();
      store.createWorkspace();
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);
    }

    @Test
    void createWorkspaceCreatesWorkspace() {
      var workspace = valueEmittedBy(store.createWorkspace());
      assertThat(workspace).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(workspace.currentVersion().transaction()).isEqualTo(tx);

      var id = workspace.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, WORKSPACE));
      assertThat(found).isEqualTo(workspace);
    }

    @Test
    void findFindsWorkspace() {
      var workspace = valueEmittedBy(store.createWorkspace());
      assertThat(store.find(workspace.id())).emitted(workspace);
    }

    @Test
    void findWithTypeOnlyFindsCorrectType() {
      var workspace = valueEmittedBy(store.createWorkspace());
      for (var type : ResourceType.values()) {
        var actual = store.find(workspace.id(), type);
        if (type == ResourceType.WORKSPACE) {
          @SuppressWarnings("unchecked")
          var wsActual = (Maybe<Workspace>) actual;
          assertThat(wsActual).emitted(workspace);
        } else {
          assertThat(actual).wasEmpty();
        }
      }
    }
  }

  @Nested
  class Collections {

    private Workspace workspace;

    @BeforeEach
    void setUp() {
      workspace = valueEmittedBy(store.createWorkspace());
    }

    @Test
    void createCollectionIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction()).txid();
      store.createCollection(workspace);
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);
    }

    @Test
    void findFindsCollection() {
      var collection = valueEmittedBy(store.createCollection(workspace));
      assertThat(store.find(collection.id())).emitted(collection);
    }

    @Test
    void findWithTypeOnlyFindsCorrectType() {
      var collection = valueEmittedBy(store.createCollection(workspace));
      for (var type : ResourceType.values()) {
        var actual = store.find(collection.id(), type);
        if (type == ResourceType.COLLECTION) {
          @SuppressWarnings("unchecked")
          var wsActual = (Maybe<Collection>) actual;
          assertThat(wsActual).emitted(collection);
        } else {
          assertThat(actual).wasEmpty();
        }
      }
    }
  }

  private Predicate<Resource<?>> isTombstoneFor(Resource<?> r) {
    return (t) -> t.isLaterVersionOf(r) && t.isDeleted();
  }
}
