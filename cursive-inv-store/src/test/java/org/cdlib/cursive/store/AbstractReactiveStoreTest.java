package org.cdlib.cursive.store;

import io.reactivex.Observable;
import org.cdlib.cursive.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.cdlib.cursive.util.RxJavaAssertions.assertThat;

public abstract class AbstractReactiveStoreTest<S extends ReactiveStore> {
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
      Observable<CWorkspace> workspaces = store.workspaces();
      assertThat(workspaces).isComplete();
      assertThat(workspaces).isEmpty();
      assertThat(workspaces).hasNoErrors();
    }

//    @Test
//    void createWorkspaceCreatesAWorkspace() {
//      CWorkspace workspace = store.createWorkspace();
//      Assertions.assertThat(workspace).isNotNull();
//      assertThat(store.workspaces()).contains(workspace);
//    }
//
//    @Test
//    void newWorkspaceIsEmpty() {
//      CWorkspace workspace = store.createWorkspace();
//      assertThat(workspace.memberCollections()).isEmpty();
//    }
//
//    @Test
//    void createChildCollectionCreatesACollection() {
//      CWorkspace workspace = store.createWorkspace();
//      CCollection collection = workspace.createCollection();
//      assertThat(workspace.memberCollections()).contains(collection);
//      assertThat(collection.parentWorkspace()).contains(workspace);
//      assertThat(store.collections()).contains(collection);
//    }
  }

//  @Nested
//  @SuppressWarnings("unused")
//  class Collections {
//    @Test
//    void collectionsEmptyByDefault() {
//      Traversable<CCollection> collections = store.collections();
//      assertThat(collections).isEmpty();
//    }
//
//    @Test
//    void createCollectionCreatesACollection() {
//      CCollection collection = store.createCollection();
//      Assertions.assertThat(collection).isNotNull();
//      assertThat(store.collections()).contains(collection);
//    }
//
//    @Test
//    void createChildCollectionCreatesACollection() {
//      CCollection parent = store.createCollection();
//      CCollection child = parent.createCollection();
//      Assertions.assertThat(child).isNotNull();
//      assertThat(parent.memberCollections()).contains(child);
//      assertThat(child.parentCollection()).contains(parent);
//      assertThat(store.collections()).contains(child);
//    }
//
//    @Test
//    void createChildObjectCreatesAnObject() {
//      CCollection parent = store.createCollection();
//      CObject child = parent.createObject();
//      Assertions.assertThat(child).isNotNull();
//      assertThat(parent.memberObjects()).contains(child);
//      assertThat(child.parentCollection()).contains(parent);
//      assertThat(store.objects()).contains(child);
//    }
//  }
//
//  @Nested
//  @SuppressWarnings("unused")
//  class Objects {
//    @Test
//    void objectsEmptyByDefault() {
//      Traversable<CObject> objects = store.objects();
//      assertThat(objects).isEmpty();
//    }
//
//    @Test
//    void createObjectCreatesAnObject() {
//      CObject object = store.createObject();
//      Assertions.assertThat(object).isNotNull();
//      assertThat(store.objects()).contains(object);
//    }
//
//    @Test
//    void createChildObjectCreatesAnObject() {
//      CObject parent = store.createObject();
//      CObject child = parent.createObject();
//      Assertions.assertThat(child).isNotNull();
//      assertThat(parent.memberObjects()).contains(child);
//      assertThat(child.parentObject()).contains(parent);
//      assertThat(store.objects()).contains(child);
//    }
//
//    @Test
//    void createChildFileCreatesAFile() {
//      CObject parent = store.createObject();
//      CFile child = parent.createFile();
//      Assertions.assertThat(child).isNotNull();
//      assertThat(parent.memberFiles()).contains(child);
//      Assertions.assertThat(child.parentObject()).isEqualTo(parent);
//      assertThat(store.files()).contains(child);
//    }
//  }
//
//  @Nested
//  @SuppressWarnings("unused")
//  class Files {
//    @Test
//    void filesEmptyByDefault() {
//      Traversable<CFile> files = store.files();
//      assertThat(files).isEmpty();
//    }
//  }
//
//  @Nested
//  @SuppressWarnings("unused")
//  class Relations {
//    @Test
//    void relationsEmptyByDefault() {
//      Traversable<CRelation> relations = store.relations();
//      Assertions.assertThat(relations.isEmpty());
//    }
//
//    @Test
//    void createRelationCreatesARelation() {
//      CObject fromObject = store.createObject();
//      CObject toObject = store.createObject();
//
//      CRelation relation = fromObject.relateTo(toObject);
//      Assertions.assertThat(relation.fromObject()).isSameAs(fromObject);
//      Assertions.assertThat(relation.toObject()).isSameAs(toObject);
//
//      assertThat(fromObject.relatedObjects()).contains(toObject);
//      assertThat(fromObject.outgoingRelations()).contains(relation);
//      assertThat(toObject.incomingRelations()).contains(relation);
//    }
//  }
}
