package org.cdlib.cursive.store;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.cdlib.cursive.util.VavrAssertions.assertThat;

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

    @Test
    void createWorkspaceCreatesAWorkspace() {
      CWorkspace workspace = store.createWorkspace();
      assertThat(workspace).isNotNull();
      assertThat(store.workspaces()).contains(workspace);
    }

    @Test
    void newWorkspaceIsEmpty() {
      CWorkspace workspace = store.createWorkspace();
      assertThat(workspace.memberCollections()).isEmpty();
    }

    @Test
    void createChildCollectionCreatesACollection() {
      CWorkspace workspace = store.createWorkspace();
      CCollection collection = workspace.createCollection();
      assertThat(workspace.memberCollections()).contains(collection);
      assertThat(collection.parentWorkspace()).contains(workspace);
      assertThat(store.collections()).contains(collection);
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

    @Test
    void createCollectionCreatesACollection() {
      CCollection collection = store.createCollection();
      assertThat(collection).isNotNull();
      assertThat(store.collections()).contains(collection);
    }

    @Test
    void createChildCollectionCreatesACollection() {
      CCollection parent = store.createCollection();
      CCollection child = parent.createCollection();
      assertThat(child).isNotNull();
      assertThat(parent.memberCollections()).contains(child);
      assertThat(child.parentCollection()).contains(parent);
      assertThat(store.collections()).contains(child);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      CCollection parent = store.createCollection();
      CObject child = parent.createObject();
      assertThat(child).isNotNull();
      assertThat(parent.memberObjects()).contains(child);
      assertThat(child.parentCollection()).contains(parent);
      assertThat(store.objects()).contains(child);
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

    @Test
    void createObjectCreatesAnObject() {
      CObject object = store.createObject();
      assertThat(object).isNotNull();
      assertThat(store.objects()).contains(object);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      CObject parent = store.createObject();
      CObject child = parent.createObject();
      assertThat(child).isNotNull();
      assertThat(parent.memberObjects()).contains(child);
      assertThat(child.parentObject()).contains(parent);
      assertThat(store.objects()).contains(child);
    }

    @Test
    void createChildFileCreatesAFile() {
      CObject parent = store.createObject();
      CFile child = parent.createFile();
      assertThat(child).isNotNull();
      assertThat(parent.memberFiles()).contains(child);
      assertThat(child.parentObject()).isEqualTo(parent);
      assertThat(store.files()).contains(child);
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

  @Nested
  @SuppressWarnings("unused")
  class Relations {
    @Test
    void relationsEmptyByDefault() {
      Traversable<CRelation> relations = store.relations();
      assertThat(relations.isEmpty());
    }

    @Test
    void createRelationCreatesARelation() {
      CObject fromObject = store.createObject();
      CObject toObject = store.createObject();

      CRelation relation = fromObject.relateTo(toObject);
      assertThat(relation.fromObject()).isSameAs(fromObject);
      assertThat(relation.toObject()).isSameAs(toObject);

      assertThat(fromObject.relatedObjects()).contains(toObject);
      assertThat(fromObject.outgoingRelations()).contains(relation);
      assertThat(toObject.incomingRelations()).contains(relation);
    }
  }
}
