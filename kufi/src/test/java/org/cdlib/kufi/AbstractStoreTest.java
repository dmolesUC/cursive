package org.cdlib.kufi;

import io.reactivex.Single;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.cdlib.cursive.util.RxAssertions.*;
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
  private static Seq<Arguments> parentToChildTypes() {
    return ResourceType.values().flatMap(p -> p.allowableChildren().map(c -> Arguments.of(p, c)));
  }

  // ------------------------------------------------------------
  // Tests

  @Nested
  class Resources {
    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void resourcesAreNotEqualToNull(ResourceType<?> type) {
      var res = valueEmittedBy(create(type));
      assertThat(res).isNotEqualTo(null);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void resourcesAreEqualToThemselves(ResourceType<?> type) {
      var res = valueEmittedBy(create(type));
      assertThat(res).isEqualTo(res);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void resourcesAreEqualToRetrievedCopiesOfThemselves(ResourceType<?> type) {
      var res0 = valueEmittedBy(create(type));
      var res1 = valueEmittedBy(store.find(res0.id()));
      assertThat(res0).isEqualTo(res1);
      assertThat(res1).isEqualTo(res0);
    }

// TODO: replace this test
//    @ParameterizedTest
//    @MethodSource("org.cdlib.kufi.ResourceType#values")
//    void resourcesAreNotEqualToDifferentVersionsOfThemselves(ResourceType<?> type) {
//      var res0 = valueEmittedBy(create(type));
//      var res1 = res0.nextVersion(res0.currentVersion().transaction().next());
//      assertThat(res0).isNotEqualTo(res1);
//      assertThat(res1).isNotEqualTo(res0);
//    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void differentResourceTypesAreDifferent(ResourceType<?> type) {
      var res0 = valueEmittedBy(create(type));
      for (var otherType: ResourceType.values().filter(t -> !type.equals(t))) {
        var res1 = valueEmittedBy(create(otherType));
        assertThat(res0).isNotEqualTo(res1);
        assertThat(res1).isNotEqualTo(res0);
      }
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void differentResourcesAreDifferent(ResourceType<?> type) {
      var res0 = valueEmittedBy(create(type));
      var res1 = valueEmittedBy(create(type));
      assertThat(res0).isNotEqualTo(res1);
      assertThat(res1).isNotEqualTo(res0);
    }

// TODO: replace this test
//    @ParameterizedTest
//    @MethodSource("org.cdlib.kufi.ResourceType#values")
//    void laterVersionsAreLaterAndEarlierVersionsAreEarlier(ResourceType<?> type) {
//      var res0 = valueEmittedBy(create(type));
//      var res1 = res0.nextVersion(res0.currentVersion().transaction().next());
//      assertThat(res0.isEarlierVersionOf(res1)).isTrue();
//      assertThat(res1.isLaterVersionOf(res0)).isTrue();
//      assertThat(res0.isLaterVersionOf(res1)).isFalse();
//      assertThat(res1.isEarlierVersionOf(res0)).isFalse();
//    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void unrelatedResourcesAreNotLaterOrEarlierVersions(ResourceType<?> type) {
      var res0 = valueEmittedBy(create(type));
      var res1 = valueEmittedBy(create(type));
      assertThat(res0.isEarlierVersionOf(res1)).isFalse();
      assertThat(res0.isLaterVersionOf(res1)).isFalse();
      assertThat(res1.isEarlierVersionOf(res0)).isFalse();
      assertThat(res1.isLaterVersionOf(res0)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void tombstonesAreLaterVersions(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      var tombstone = valueEmittedBy(delete(resource));
      assertThat(resource.isEarlierVersionOf(tombstone)).isTrue();
      assertThat(tombstone.isLaterVersionOf(resource)).isTrue();
      assertThat(resource.isLaterVersionOf(tombstone)).isFalse();
      assertThat(tombstone.isEarlierVersionOf(resource)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void deletingTombstonesIsHarmless(ResourceType<?> type) {
      var tombstone0 = valueEmittedBy(create(type).flatMap(AbstractStoreTest.this::delete));
      var tombstone1 = valueEmittedBy(delete(tombstone0));
      assertThat(tombstone0).isEqualTo(tombstone1);
      assertThat(tombstone1).isEqualTo(tombstone0);
    }
  }

  @Nested
  class Workspaces {
    @Test
    void childrenAppearInChildrenList() {
      var ws = valueEmittedBy(store.createWorkspace());
      var col = valueEmittedBy(store.createCollection(ws));
      assertThat(ws.childCollections()).emitted(col);
    }
  }

  @Nested
  class Collections {
    @Test
    void childrenAppearInChildrenList() {
      var c1 = valueEmittedBy(store.createWorkspace().flatMap(store::createCollection));
      var c2 = valueEmittedBy(store.createCollection(c1));
      assertThat(c1.childCollections()).emitted(c2);
    }

    @Test
    void parentFindsParent() {
      var ws = valueEmittedBy(store.createWorkspace());
      var col = valueEmittedBy(store.createCollection(ws));
      assertThat(col.parent().map(Either::getLeft)).emittedValueThat(isLaterVersionOf(ws));
    }

    @Test
    void parentIsNotConfusedByChildren() {
      var ws = valueEmittedBy(store.createWorkspace());
      var c1 = valueEmittedBy(store.createCollection(ws));
      var c2 = valueEmittedBy(store.createCollection(c1));
      assertThat(c1.parent().map(Either::getLeft)).emittedValueThat(isLaterVersionOf(ws));
      assertThat(c2.parent().map(Either::get)).emittedValueThat(isLaterVersionOf(c1));
    }
  }

  @Nested
  class CRUD {
    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
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
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void findFinds(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      assertThat(store.find(resource.id())).emitted(resource);
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
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

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void deleteCreatesTombstone(ResourceType<?> type) {
      var res = valueEmittedBy(create(type));
      assertThat(delete(res)).emittedValueThat(isTombstoneFor(res));
    }
  }

  @Nested
  class ResourceTypes {
    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void typesCanCastToTheirImplType(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      var castResource = type.cast(resource);
      assertThat(castResource).isInstanceOf(type.implType());
    }

    @ParameterizedTest
    @MethodSource("org.cdlib.kufi.ResourceType#values")
    void typesCannotCastToWrongImplType(ResourceType<?> type) {
      var resource = valueEmittedBy(create(type));
      for (var wrongType: ResourceType.values().filter(t -> !type.equals(t))){
        assertThatIllegalArgumentException().isThrownBy(() -> {
          wrongType.cast(resource);
        });
      }
    }
  }

  @Nested
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
      
      var linksToChild = valuesEmittedBy(store.linksTo(child.id()));
      var linksToParent = valuesEmittedBy(store.linksTo(parent.id()));

      assertThat(linksToChild).contains(expectedParentChildLink);
      assertThat(linksToParent).contains(expectedChildParentLink);
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

  private Predicate<Resource<?>> isLaterVersionOf(Resource<?> r) {
    return (r1) -> r1.isLaterVersionOf(r);
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
    var parentType = parentToChildTypes()
      .map(Arguments::get)
      .filter(a -> a[1] == childType)
      .map(a -> (ResourceType<?>) a[0])
      .head();
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
