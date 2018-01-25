package org.cdlib.cursive.store;

import io.vavr.collection.Array;
import io.vavr.collection.Traversable;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.Workspace;
import org.cdlib.cursive.pcdm.PcdmCollection;
import org.cdlib.cursive.pcdm.PcdmFile;
import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.pcdm.PcdmRelation;
import org.cdlib.cursive.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    void createWorkspaceCreatesMultipleWorkspaces() {
      Workspace w1 = store.createWorkspace();
      Workspace w2 = store.createWorkspace();
      Array<Workspace> workspaces = store.workspaces().toArray();
      assertThat(workspaces).containsOnly(w1, w2);
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
      assertThat(store.allCollections()).contains(collection);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Collections {
    @Test
    void collectionsEmptyByDefault() {
      Traversable<PcdmCollection> collections = store.allCollections();
      assertThat(collections).isEmpty();
    }

    @Test
    void createCollectionCreatesACollection() {
      PcdmCollection collection = store.createCollection();
      assertThat(collection).isNotNull();
      assertThat(store.allCollections()).contains(collection);
    }

    @Test
    void createChildCollectionCreatesACollection() {
      PcdmCollection parent = store.createCollection();
      PcdmCollection child = parent.createCollection();
      assertThat(child).isNotNull();
      assertThat(parent.memberCollections()).contains(child);
      assertThat(child.parentCollection()).contains(parent);
      assertThat(store.allCollections()).contains(child);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      PcdmCollection parent = store.createCollection();
      PcdmObject child = parent.createObject();
      assertThat(child).isNotNull();
      assertThat(parent.memberObjects()).contains(child);
      assertThat(child.parentCollection()).contains(parent);
      assertThat(store.allObjects()).contains(child);
    }

    @Test
    void collectionsFoundAtVariousLevels() {
      PcdmCollection c1 = store.createCollection();
      PcdmCollection c2 = store.createWorkspace().createCollection();
      PcdmCollection c3 = c1.createCollection();
      PcdmCollection c4 = c2.createCollection();
      PcdmCollection c5 = c3.createCollection();
      assertThat(store.allCollections()).containsOnly(c1, c2, c3, c4, c5);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Objects {
    @Test
    void objectsEmptyByDefault() {
      Traversable<PcdmObject> objects = store.allObjects();
      assertThat(objects).isEmpty();
    }

    @Test
    void createObjectCreatesAnObject() {
      PcdmObject object = store.createObject();
      assertThat(object).isNotNull();
      assertThat(store.allObjects()).contains(object);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      PcdmObject parent = store.createObject();
      PcdmObject child = parent.createObject();
      assertThat(child).isNotNull();
      assertThat(parent.memberObjects()).contains(child);
      assertThat(child.parentObject()).contains(parent);
      assertThat(store.allObjects()).contains(child);
    }

    @Test
    void createChildFileCreatesAFile() {
      PcdmObject parent = store.createObject();
      PcdmFile child = parent.createFile();
      assertThat(child).isNotNull();
      assertThat(parent.memberFiles()).contains(child);
      assertThat(child.parentObject()).isEqualTo(parent);
      assertThat(store.allFiles()).contains(child);
    }

    @Test
    void objectsFoundAtVariousLevels() {
      Workspace w1 = store.createWorkspace();
      PcdmCollection c1 = w1.createCollection();
      PcdmCollection c2 = store.createCollection();
      PcdmCollection c3 = c1.createCollection();
      PcdmCollection c4 = c3.createCollection();

      PcdmObject o1 = store.createObject();
      PcdmObject o2 = c1.createObject();
      PcdmObject o3 = c2.createObject();
      PcdmObject o4 = o1.createObject();
      PcdmObject o5 = o2.createObject();
      PcdmObject o6 = o3.createObject();
      PcdmObject o7 = o4.createObject();
      PcdmObject o8 = c3.createObject();
      PcdmObject o9 = c4.createObject();

      assertThat(store.allObjects()).containsOnly(o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Files {
    @Test
    void filesEmptyByDefault() {
      Traversable<PcdmFile> files = store.allFiles();
      assertThat(files).isEmpty();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Relations {
    @Test
    void relationsEmptyByDefault() {
      Traversable<PcdmRelation> relations = store.allRelations();
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
      assertThat(store.find(workspace.id())).contains(workspace);
    }

    @Test
    void findFindsACollection() {
      PcdmCollection collection = store.createCollection();
      assertThat(store.find(collection.id())).contains(collection);
    }

    @Test
    void findFindsAnObject() {
      PcdmObject object = store.createObject();
      assertThat(store.find(object.id())).contains(object);
    }

    @Test
    void findFindsAFile() {
      PcdmFile file = store.createObject().createFile();
      assertThat(store.find(file.id())).contains(file);
    }

    @Test
    void findFindsNothing() {
      assertThat(store.find(TestUtils.badUUID())).isEmpty();
    }
  }

}
