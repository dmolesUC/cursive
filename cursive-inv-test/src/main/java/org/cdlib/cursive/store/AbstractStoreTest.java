package org.cdlib.cursive.store;

import io.vavr.collection.Traversable;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;
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
      Traversable<Workspace> workspaces = store.workspaces();
      assertThat(workspaces).isEmpty();
    }

    @Test
    void createWorkspaceCreatesAWorkspace() {
      Workspace workspace = store.createWorkspace();
      assertThat(workspace).isNotNull();
      assertThat(store.workspaces()).contains(workspace);
    }

    @Test
    void newWorkspaceIsEmpty() {
      Workspace workspace = store.createWorkspace();
      assertThat(workspace.memberCollections()).isEmpty();
    }

    @Test
    void createChildCollectionCreatesACollection() {
      Workspace workspace = store.createWorkspace();
      PcdmCollection collection = workspace.createCollection();
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
      Traversable<PcdmCollection> collections = store.collections();
      assertThat(collections).isEmpty();
    }

    @Test
    void createCollectionCreatesACollection() {
      PcdmCollection collection = store.createCollection();
      assertThat(collection).isNotNull();
      assertThat(store.collections()).contains(collection);
    }

    @Test
    void createChildCollectionCreatesACollection() {
      PcdmCollection parent = store.createCollection();
      PcdmCollection child = parent.createCollection();
      assertThat(child).isNotNull();
      assertThat(parent.memberCollections()).contains(child);
      assertThat(child.parentCollection()).contains(parent);
      assertThat(store.collections()).contains(child);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      PcdmCollection parent = store.createCollection();
      PcdmObject child = parent.createObject();
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
      Traversable<PcdmObject> objects = store.objects();
      assertThat(objects).isEmpty();
    }

    @Test
    void createObjectCreatesAnObject() {
      PcdmObject object = store.createObject();
      assertThat(object).isNotNull();
      assertThat(store.objects()).contains(object);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      PcdmObject parent = store.createObject();
      PcdmObject child = parent.createObject();
      assertThat(child).isNotNull();
      assertThat(parent.memberObjects()).contains(child);
      assertThat(child.parentObject()).contains(parent);
      assertThat(store.objects()).contains(child);
    }

    @Test
    void createChildFileCreatesAFile() {
      PcdmObject parent = store.createObject();
      PcdmFile child = parent.createFile();
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
      Traversable<PcdmFile> files = store.files();
      assertThat(files).isEmpty();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Relations {
    @Test
    void relationsEmptyByDefault() {
      Traversable<PcdmRelation> relations = store.relations();
      assertThat(relations.isEmpty());
    }

    @Test
    void createRelationCreatesARelation() {
      PcdmObject fromObject = store.createObject();
      PcdmObject toObject = store.createObject();

      PcdmRelation relation = fromObject.relateTo(toObject);
      assertThat(relation.fromObject()).isEqualTo(fromObject);
      assertThat(relation.toObject()).isEqualTo(toObject);

      assertThat(fromObject.relatedObjects()).contains(toObject);
      assertThat(fromObject.outgoingRelations()).contains(relation);
      assertThat(toObject.incomingRelations()).contains(relation);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Find {
    @Test
    void findFindsAWorkspace() {
      Workspace workspace = store.createWorkspace();
      assertThat(store.find(workspace.identifier())).contains(workspace);
    }

    @Test
    void findFindsACollection() {
      PcdmCollection collection = store.createCollection();
      assertThat(store.find(collection.identifier())).contains(collection);
    }

    @Test
    void findFindsAnObject() {
      PcdmObject object = store.createObject();
      assertThat(store.find(object.identifier())).contains(object);
    }

    @Test
    void findFindsAFile() {
      PcdmFile file = store.createObject().createFile();
      assertThat(store.find(file.identifier())).contains(file);
    }

    @Test
    void findFindsNothing() {
      assertThat(store.find("I am not a valid identifier")).isEmpty();
    }
  }
}
