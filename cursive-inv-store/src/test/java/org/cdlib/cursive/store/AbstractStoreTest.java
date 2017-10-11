package org.cdlib.cursive.store;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.core.CFile;
import org.cdlib.cursive.core.CObject;
import org.cdlib.cursive.core.CCollection;
import org.cdlib.cursive.core.CWorkspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.cdlib.cursive.util.ValueAssert.assertThat;

public abstract class AbstractStoreTest<S extends Store> {

  // ------------------------------------------------------------
  // Fixture

  private S store;

  protected abstract S newStore();

  @BeforeEach
  void setUp() {
    store = newStore();
  }

  // ------------------------------------------------------------
  // Tests

  @Nested
  @SuppressWarnings("unused")
  class Workspaces {
    @Test
    void workspacesEmptyByDefault() {
      Traversable<CWorkspace> workspaces = store.workspaces();
      assertThat(workspaces).isEmpty();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Collections {
    @Test
    void collectionsEmptyByDefault() {
      Traversable<CCollection> collections = store.collections();
      assertThat(collections).isEmpty();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Objects {
    @Test
    void objectsEmptyByDefault() {
      Traversable<CObject> objects = store.objects();
      assertThat(objects).isEmpty();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Files {
    @Test
    void filesEmptyByDefault() {
      Traversable<CFile> files = store.files();
      assertThat(files).isEmpty();
    }
  }
}
