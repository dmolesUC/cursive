package org.cdlib.kufi;

import io.reactivex.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.cdlib.cursive.util.RxAssertions.*;
import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

public abstract class AbstractStoreTest<S extends Store> {
  protected abstract S newStore();

  private S store;

  // TODO: test dead links

  @BeforeEach
  void setUp() {
    store = newStore();
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
      var ws = valueEmittedBy(store.createWorkspace());
      assertThat(ws).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(ws.currentVersion().transaction()).isEqualTo(tx);

      var id = ws.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, WORKSPACE));
      assertThat(found).isEqualTo(ws);
    }

    @Test
    void findFindsWorkspace() {
      var ws = valueEmittedBy(store.createWorkspace());
      assertThat(store.find(ws.id())).emitted(ws);
    }

    @Test
    void findWithTypeOnlyFindsCorrectType() {
      var ws = valueEmittedBy(store.createWorkspace());
      for (var type : ResourceType.values()) {
        var actual = store.find(ws.id(), type);
        if (type == ResourceType.WORKSPACE) {
          @SuppressWarnings("unchecked")
          var wsActual = (Maybe<Workspace>) actual;
          assertThat(wsActual).emitted(ws);
        } else {
          assertThat(actual).wasEmpty();
        }
      }
    }

    @Test
    void deleteWorkspaceDeletesEmpty() {
      var ws = valueEmittedBy(store.createWorkspace());
      var tx = ws.currentVersion().transaction().txid();

      var result = store.deleteWorkspace(ws);
      assertThat(result).isComplete();

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);

      assertThat(store.find(ws.id(), WORKSPACE)).wasEmpty();

      assertThat(store.findTombstone(ws.id())).emittedValueThat(isDeleted(ws));
      assertThat(store.findTombstone(ws.id(), WORKSPACE)).emittedValueThat(isDeleted(ws));
    }

    @Test
    void deleteWorkspaceFailsWithChildren() {
      var parent = valueEmittedBy(store.createWorkspace());
      var child = valueEmittedBy(store.createCollection(parent));

      var parentNext = valueEmittedBy(store.find(parent.id(), WORKSPACE));
      var tx = valueEmittedBy(store.transaction());

      var result = store.deleteWorkspace(parent);
      var error = errorEmittedBy(result);
      assertThat(error).isNotNull();

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx);

      var found = valueEmittedBy(store.find(parent.id(), WORKSPACE));
      assertThat(found).isEqualTo(parentNext);

      var children = valuesEmittedBy(parentNext.childCollections());
      assertThat(children).contains(child);

      var collections = valueEmittedBy(child.parent());
      assertThat(collections.left()).contains(parentNext);

      assertThat(store.findTombstone(parent.id())).wasEmpty();
      assertThat(store.findTombstone(parent.id(), WORKSPACE)).wasEmpty();
    }

    @Test
    void deleteWorkspaceRecursiveDeletesChildren() {
      var parent = valueEmittedBy(store.createWorkspace());
      var child = valueEmittedBy(store.createCollection(parent));
      var grandchild = valueEmittedBy(store.createCollection(child));
      var tx = valueEmittedBy(store.transaction()).txid();

      var result = store.deleteWorkspace(parent, true);
      assertThat(result).isComplete();

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);

      assertThat(store.find(parent.id(), WORKSPACE)).wasEmpty();
      assertThat(store.findTombstone(parent.id())).emittedValueThat(isDeleted(parent));
      assertThat(store.findTombstone(parent.id(), WORKSPACE)).emittedValueThat(isDeleted(parent));

      assertThat(store.find(child.id(), COLLECTION)).wasEmpty();
      assertThat(store.findTombstone(child.id())).emittedValueThat(isDeleted(child));
      assertThat(store.findTombstone(child.id(), COLLECTION)).emittedValueThat(isDeleted(child));

      assertThat(store.find(grandchild.id(), COLLECTION)).wasEmpty();
      assertThat(store.findTombstone(grandchild.id())).emittedValueThat(isDeleted(grandchild));
      assertThat(store.findTombstone(grandchild.id(), COLLECTION)).emittedValueThat(isDeleted(grandchild));

      assertThat(parent.childCollections().test()).observedNothing();
      assertThat(child.childCollections().test()).observedNothing();

      assertThat(child.parent()).emittedOneError();
      assertThat(grandchild.parent()).emittedOneError();
    }
  }

  @Nested
  class Collections {

    private Workspace ws;

    @BeforeEach
    void setUp() {
      ws = valueEmittedBy(store.createWorkspace());
    }

    @Test
    void createChildFailsWithTombstonedParentWorkspace() {
      store.deleteWorkspace(ws);
      var tx = valueEmittedBy(store.transaction());
      assertThat(store.createCollection(ws)).emittedOneError();
      assertThat(store.transaction()).emitted(tx);
    }

    @Test
    void createChildFailsWithTombstonedParentCollection() {
      var coll = valueEmittedBy(store.createCollection(ws));
      store.deleteCollection(coll);
      var tx = valueEmittedBy(store.transaction());
      assertThat(store.createCollection(coll)).emittedOneError();
      assertThat(store.transaction()).emitted(tx);
    }

    @Test
    void createCollectionIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction()).txid();
      store.createCollection(ws);
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);
    }

    @Test
    void findFindsCollection() {
      var coll = valueEmittedBy(store.createCollection(ws));
      assertThat(store.find(coll.id())).emitted(coll);
    }

    @Test
    void createCollectionCreatesCollectionAsWorkspaceChild() {
      var child = valueEmittedBy(store.createCollection(ws));
      assertThat(child).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(child.currentVersion().transaction()).isEqualTo(tx);
      var parentNext = ws.nextVersion(tx);

      var id = child.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, COLLECTION));
      assertThat(found).isEqualTo(child);

      var children = valuesEmittedBy(parentNext.childCollections());
      assertThat(children).contains(child);

      var childParent = valueEmittedBy(child.parent());
      assertThat(childParent.left()).contains(parentNext);
    }

    @Test
    void createCollectionCreatesCollectionAsCollectionChild() {
      var parent = valueEmittedBy(store.createCollection(ws));

      var child = valueEmittedBy(store.createCollection(parent));
      assertThat(child).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(child.currentVersion().transaction()).isEqualTo(tx);
      var parentNext = parent.nextVersion(tx);

      var id = child.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, COLLECTION));
      assertThat(found).isEqualTo(child);

      var children = valuesEmittedBy(parentNext.childCollections());
      assertThat(children).contains(child);

      var childParent = valueEmittedBy(child.parent());
      assertThat(childParent.right()).contains(parentNext);
    }

    @Test
    void deleteCollectionFailsWithChildren() {
      var parent = valueEmittedBy(store.createCollection(ws));
      var child = valueEmittedBy(store.createCollection(parent));

      var parentNext = valueEmittedBy(store.find(parent.id(), COLLECTION));
      var tx = valueEmittedBy(store.transaction());

      var result = store.deleteCollection(parent);
      var error = errorEmittedBy(result);
      assertThat(error).isNotNull();

      assertThat(store.findTombstone(parent.id())).wasEmpty();
      assertThat(store.findTombstone(parent.id(), COLLECTION)).wasEmpty();

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx);

      var found = valueEmittedBy(store.find(parent.id(), COLLECTION));
      assertThat(found).isEqualTo(parentNext);

      var children = valuesEmittedBy(parentNext.childCollections());
      assertThat(children).contains(child);

      var collections = valueEmittedBy(child.parent());
      assertThat(collections.right()).contains(parentNext);
    }

    @Test
    void deleteCollectionRecursiveDeletesChildren() {
      var parent = valueEmittedBy(store.createCollection(ws));
      var child = valueEmittedBy(store.createCollection(parent));
      var grandchild = valueEmittedBy(store.createCollection(child));
      var tx = valueEmittedBy(store.transaction()).txid();

      var result = store.deleteCollection(parent, true);
      assertThat(result).isComplete();
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx.txid()).isEqualTo(tx + 1);

      assertThat(store.find(parent.id(), WORKSPACE)).wasEmpty();
      assertThat(store.findTombstone(parent.id())).emittedValueThat(isDeleted(parent));
      assertThat(store.findTombstone(parent.id(), COLLECTION)).emittedValueThat(isDeleted(parent));

      assertThat(store.find(child.id(), COLLECTION)).wasEmpty();
      assertThat(store.findTombstone(child.id())).emittedValueThat(isDeleted(child));
      assertThat(store.findTombstone(child.id(), COLLECTION)).emittedValueThat(isDeleted(child));

      assertThat(store.find(grandchild.id(), COLLECTION)).wasEmpty();
      assertThat(store.findTombstone(grandchild.id())).emittedValueThat(isDeleted(grandchild));
      assertThat(store.findTombstone(grandchild.id(), COLLECTION)).emittedValueThat(isDeleted(grandchild));

      assertThat(parent.childCollections().test()).observedNothing();
      assertThat(child.childCollections().test()).observedNothing();

      assertThat(child.parent()).emittedOneError();
      assertThat(grandchild.parent()).emittedOneError();
    }
  }

  private Predicate<Resource<?>> isDeleted(Resource<?> r) {
    return (t) -> t.isLaterVersionOf(r) && t.isDeleted();
  }
}
