package org.cdlib.kufi;

import io.reactivex.Single;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.cdlib.cursive.util.RxAssertions.assertThat;
import static org.cdlib.cursive.util.RxAssertions.valueEmittedBy;
import static org.cdlib.cursive.util.RxAssertions.valuesEmittedBy;
import static org.cdlib.kufi.LinkType.CHILD_OF;
import static org.cdlib.kufi.LinkType.PARENT_OF;
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
  // TODO: explicitly test all interface methods (even if that reduplicates some parameterized stuff)

  @BeforeEach
  void setUp() {
    store = newStore();
  }

  // -----------------------------
  // Parameterized test data

  /** {@link MethodSource} for {@link AbstractStoreTest.ParentsAndChildren} */
  @SuppressWarnings("unused")
  private static Stream<Arguments> parentToChildTypes() {
    return ResourceType.values().flatMap(p -> p.allowableChildren().map(c -> Arguments.of(p, c))).toJavaStream();
  }

  /** {@link MethodSource} for {@link AbstractStoreTest.CreateAndFind} */
  @SuppressWarnings("unused")
  private static Stream<ResourceType<?>> allTypes() {
    return ResourceType.values().toJavaStream();
  }

  // ------------------------------------------------------------
  // Tests

  @Nested
  @SuppressWarnings("JUnit5MalformedParameterized")
  class CreateAndFind {
    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#allTypes")
    void createCreates(ResourceType<?> type) {
      var tx = valueEmittedBy(store.transaction());

      var resource = valueEmittedBy(create(type));
      assertThat(resource.hasType(type)).isTrue();

      var resultVersion = resource.currentVersion();
      assertThat(resultVersion.vid()).isEqualTo(0L);

      var resultTx = resultVersion.transaction();
      assertThat(resultTx).isGreaterThan(tx);

      var txNext = valueEmittedBy(store.transaction());
      assertThat(txNext).isEqualTo(resultTx);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#allTypes")
    void findFinds(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      assertThat(store.find(resource.id())).emitted(resource);

    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#allTypes")
    void findFindsOnlyCorrectType(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      for (var t1 : ResourceType.values()) {
        var actual = store.find(resource.id(), t1);
        if (t1 == type) {
          assertThat(valueEmittedBy(actual)).isEqualTo(resource);
        } else {
          assertThat(actual).wasEmpty();
        }
      }
    }
  }

  @Nested
  @SuppressWarnings("JUnit5MalformedParameterized")
  class ParentsAndChildren {
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

      var parentLinks = valuesEmittedBy(store.linksFrom(parent.id()));
      var childLinks = valuesEmittedBy(store.linksFrom(child.id()));

      var expectedParentChildLink = Link.create(parentNext, PARENT_OF, child, tx);
      var expectedChildParentLink = Link.create(child, CHILD_OF, parentNext, tx);

      assertThat(parentLinks).contains(expectedParentChildLink);
      assertThat(childLinks).contains(expectedChildParentLink);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void parentChildLink(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);
      var child = valueEmittedBy(create(parent, childType));
      var tx = child.currentVersion().transaction();
      var parentNext = valueEmittedBy(store.find(parent.id(), parentType));

      var parentLinks = valuesEmittedBy(store.linksFrom(parent.id()));
      var childLinks = valuesEmittedBy(store.linksFrom(child.id()));

      var parentLink = parentLinks.find(l -> l.type() == PARENT_OF).get();
      assertThat(parentLink.source()).isEqualTo(parentNext);
      assertThat(parentLink.target()).isEqualTo(child);
      assertThat(parentLink.isLive()).isTrue();
      assertThat(parentLink.createdAt()).isEqualTo(tx);
      assertThat(parentLink.deletedAt()).isEmpty();

      var childLink = childLinks.find(l -> l.type() == CHILD_OF).get();
      assertThat(childLink.source()).isEqualTo(child);
      assertThat(childLink.target()).isEqualTo(parentNext);
      assertThat(childLink.isLive()).isTrue();
      assertThat(childLink.createdAt()).isEqualTo(tx);
      assertThat(childLink.deletedAt()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.AbstractStoreTest#parentToChildTypes")
    void createChildFailsWithTombstonedParent(ResourceType<?> parentType, ResourceType<?> childType) {
      var parent = createParent(parentType);
      var tombstone = valueEmittedBy(delete(parent));
      var tx = valueEmittedBy(store.transaction());
      assertThat(tombstone.deletedAtTransaction()).contains(tx);

      assertThat(create(parent, childType)).emittedOneError();
      assertThat(store.transaction()).emitted(tx);
      verifyTombstone(parent);
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
      var txNext = valueEmittedBy(store.transaction());
      assertThat(txNext).isGreaterThan(tx);

      var parentTombstone = verifyTombstone(parent);
      assertThat(parentTombstone.deletedAtTransaction()).contains(txNext);

      var childTombstone = verifyTombstone(child);
      assertThat(childTombstone.deletedAtTransaction()).contains(txNext);

      var parentLinks = valuesEmittedBy(store.linksFrom(parent.id()));
      var childLinks = valuesEmittedBy(store.linksFrom(child.id()));

      var expectedParentChildLink = Link.create(parentTombstone, PARENT_OF, childTombstone, tx, txNext);
      var expectedChildParentLink = Link.create(childTombstone, CHILD_OF, parentTombstone, tx, txNext);

      assertThat(parentLinks).contains(expectedParentChildLink);
      assertThat(childLinks).contains(expectedChildParentLink);
    }

    Resource<?> verifyTombstone(Resource<?> r) {
      var id = r.id();
      var maybeTombstone = store.findTombstone(id);
      assertThat(maybeTombstone).emittedValueThat(isTombstoneFor(r));
      assertThat(store.findTombstone(id, r.type())).emittedValueThat(isTombstoneFor(r));
      return valueEmittedBy(maybeTombstone);
    }
  }

  // ------------------------------------------------------------
  // Helper methods

  private Predicate<Resource<?>> isTombstoneFor(Resource<?> r) {
    return (t) -> t.isLaterVersionOf(r) && t.isDeleted();
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
    if (parent.hasType(WORKSPACE) && childType == COLLECTION) {
      return store.createCollection((Workspace) parent);
    }
    if (parent.hasType(COLLECTION) && childType == COLLECTION) {
      return store.createCollection((Collection) parent);
    }
    throw new IllegalArgumentException("Can't create " + childType + " as child of " + parent.type());
  }

  private Single<? extends Resource<?>> create(ResourceType<?> childType) {
    if (childType == WORKSPACE) {
      return store.createWorkspace();
    }
    var parentType = Option.ofOptional(
      parentToChildTypes().map(Arguments::get)
        .filter(a -> a[1] == childType)
        .map(a -> (ResourceType<?>) a[0])
        .findFirst()
    ).get();
    var parent = createParent(parentType);
    return create(parent, childType);
  }

  private Single<? extends Resource<?>> delete(Resource<?> r) {
    if (r.hasType(WORKSPACE)) {
      return store.deleteWorkspace((Workspace) r);
    }
    if (r.hasType(COLLECTION)) {
      return store.deleteCollection((Collection) r);
    }
    throw new IllegalArgumentException("Can't delete " + r.type());
  }

  private Single<? extends Resource<?>> deleteRecursive(Resource<?> r) {
    if (r.hasType(WORKSPACE)) {
      return store.deleteWorkspace((Workspace) r, true);
    }
    if (r.hasType(COLLECTION)) {
      return store.deleteCollection((Collection) r, true);
    }
    throw new IllegalArgumentException("Can't delete " + r.type());
  }
}
