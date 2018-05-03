package org.cdlib.kufi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cdlib.cursive.util.RxAssertions.valueEmittedBy;
import static org.cdlib.cursive.util.RxAssertions.valuesEmittedBy;
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
  }

  @Nested
  class Collections {

    private Workspace workspace;

    @BeforeEach
    void setUp() {
      workspace = valueEmittedBy(store.createWorkspace());
    }

    @Test
    void newCollectionIncrementsTransaction() {
      var tx = valueEmittedBy(store.transaction());
      store.createCollection(workspace);
      var newTx = valueEmittedBy(store.transaction());
      assertThat(newTx).isEqualTo(tx + 1);
    }

    @Test
    void newCollectionCreatesCollection() {
      var coll = valueEmittedBy(store.createCollection(workspace));
      assertThat(coll).isNotNull();

      var tx = valueEmittedBy(store.transaction());
      assertThat(coll.transaction()).isEqualTo(tx);

      var id = coll.id();
      assertThat(id).isNotNull();

      var found = valueEmittedBy(store.find(id, COLLECTION));
      assertThat(found).isEqualTo(coll);

      var children = valuesEmittedBy(workspace.childCollections());
      assertThat(children).contains(coll);
    }
  }
}
