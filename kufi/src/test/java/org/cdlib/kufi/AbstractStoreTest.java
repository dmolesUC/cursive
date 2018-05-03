package org.cdlib.kufi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.cdlib.cursive.util.RxAssertions.*;
import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

public abstract class AbstractStoreTest<S extends Store> {
  protected abstract S newStore();

  S store;

  @BeforeEach
  void setUp() {
    store = newStore();
  }

  @Nested
  class Workspaces {
    @Test
    void newWorkspaceIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction());
      store.createWorkspace();
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx + 1);
    }

    @Test
    void newWorkspaceCreatesWorkspace() {
      var ws = valueEmittedBy(store.createWorkspace());
      assertThat(ws).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(ws.transaction()).isEqualTo(tx);

      var id = ws.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, WORKSPACE));
      assertThat(found).isEqualTo(ws);
    }

    @Test
    void deleteWorkspaceDeletesEmpty() {
      var ws = valueEmittedBy(store.createWorkspace());
      var tx = ws.transaction();

      var result = store.deleteWorkspace(ws);
      assertThat(completed(result)).isTrue(); // TODO: clean this up

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx + 1);

      assertThat(store.find(ws.id(), WORKSPACE).test()).observedNothing(); // TODO: clean this up
    }

    @Test
    void deleteWorkspaceFailsWithChildren() {
      var ws = valueEmittedBy(store.createWorkspace());
      var coll = valueEmittedBy(store.createCollection(ws));
      var tx = valueEmittedBy(store.transaction());

      var result = store.deleteWorkspace(ws);
      var error = errorEmittedBy(result);
      assertThat(error).isNotNull();

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx);

      var found = valueEmittedBy(store.find(ws.id(), WORKSPACE));
      assertThat(found).isEqualTo(ws);

      var children = valuesEmittedBy(ws.childCollections());
      assertThat(children).contains(coll);

      var collections = valueEmittedBy(coll.parent());
      assertThat(collections.left()).contains(ws);
    }

    @Test
    void deleteWorkspaceRecursiveDeletesChildren() {
      var ws = valueEmittedBy(store.createWorkspace());
      var coll = valueEmittedBy(store.createCollection(ws));
      var tx = valueEmittedBy(store.transaction());

      var result = store.deleteWorkspace(ws, true);
      assertThat(completed(result)).isTrue(); // TODO: clean this up

      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx + 1);

      assertThat(store.find(ws.id(), WORKSPACE).test()).observedNothing(); // TODO: clean this up
      assertThat(store.find(coll.id(), COLLECTION).test()).observedNothing(); // TODO: clean this up

      assertThat(ws.childCollections().test()).observedNothing();

      var res1 = coll.parent();
      var error = errorEmittedBy(res1);
      assertThat(error).isNotNull();

      // TODO: figure out how to track deletes (crv:HAS_MEMBER.{To, ToVersion})
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
    void newCollectionIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction());
      store.createCollection(ws);
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx + 1);
    }

    @Test
    void newCollectionCreatesCollection() {
      var coll = valueEmittedBy(store.createCollection(ws));
      assertThat(coll).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(coll.transaction()).isEqualTo(tx);

      var id = coll.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, COLLECTION));
      assertThat(found).isEqualTo(coll);

      var children = valuesEmittedBy(ws.childCollections());
      assertThat(children).contains(coll);

      var collections = valueEmittedBy(coll.parent());
      assertThat(collections.left()).contains(ws);
    }

  }
}
