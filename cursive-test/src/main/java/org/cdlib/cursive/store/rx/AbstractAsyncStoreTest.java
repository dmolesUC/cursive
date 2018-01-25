package org.cdlib.cursive.store.rx;

import io.reactivex.observers.TestObserver;
import io.vavr.collection.List;
import org.cdlib.cursive.core.async.AsyncStore;
import org.cdlib.cursive.core.async.AsyncWorkspace;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.pcdm.async.AsyncPcdmRelation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.cdlib.cursive.util.RxAssertions.*;

public abstract class AbstractAsyncStoreTest<S extends AsyncStore> {
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
      TestObserver<AsyncWorkspace> workspacesObserver = store.workspaces().test();
      assertThat(workspacesObserver).isComplete();
      assertThat(workspacesObserver).observedNothing();
      assertThat(workspacesObserver).observedNoErrors();
    }

    @Test
    void createWorkspaceCreatesAWorkspace() {
      AsyncWorkspace workspace = valueEmittedBy(store.createWorkspace());
      TestObserver<AsyncWorkspace> allWorkspacesObserver = store.workspaces().test();
      assertThat(allWorkspacesObserver).observedExactly(workspace);
    }

    @Test
    void newWorkspaceIsEmpty() {
      AsyncWorkspace workspace = valueEmittedBy(store.createWorkspace());
      TestObserver<AsyncPcdmCollection> memberCollectionsObserver = workspace.memberCollections().test();
      assertThat(memberCollectionsObserver).observedNothing();
    }

    @Test
    void createChildCollectionCreatesACollection() {
      AsyncWorkspace workspace = valueEmittedBy(store.createWorkspace());
      AsyncPcdmCollection collection = valueEmittedBy(workspace.createCollection());

      TestObserver<AsyncPcdmCollection> memberCollectionsObserver = workspace.memberCollections().test();
      assertThat(memberCollectionsObserver).observedExactly(collection);

      assertThat(valueEmittedBy(collection.parentWorkspace())).isEqualTo(workspace);

      TestObserver<AsyncPcdmCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observedExactly(collection);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Collections {
    @Test
    void collectionsEmptyByDefault() {
      List<AsyncPcdmCollection> collections = valuesEmittedBy(store.collections());
      assertThat(collections).isEmpty();
    }

    @Test
    void createCollectionCreatesACollection() {
      AsyncPcdmCollection collection = valueEmittedBy(store.createCollection());
      TestObserver<AsyncPcdmCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observedExactly(collection);
    }

    @Test
    void createChildCollectionCreatesACollection() {
      AsyncPcdmCollection parent = valueEmittedBy(store.createCollection());
      AsyncPcdmCollection child = valueEmittedBy(parent.createCollection());

      TestObserver<AsyncPcdmCollection> memberCollectionsObserver = parent.memberCollections().test();
      assertThat(memberCollectionsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentCollection())).isEqualTo(parent);

      TestObserver<AsyncPcdmCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observed(parent, child);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      AsyncPcdmCollection parent = valueEmittedBy(store.createCollection());
      AsyncPcdmObject child = valueEmittedBy(parent.createObject());

      TestObserver<AsyncPcdmObject> memberObjectsObserver = parent.memberObjects().test();
      assertThat(memberObjectsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentCollection())).isEqualTo(parent);

      TestObserver<AsyncPcdmObject> allObjectsObserver = store.objects().test();
      assertThat(allObjectsObserver).observed(child);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Objects {
    @Test
    void objectsEmptyByDefault() {
      assertThat(valuesEmittedBy(store.objects())).isEmpty();
    }

    @Test
    void createObjectCreatesAnObject() {
      AsyncPcdmObject object = valueEmittedBy(store.createObject());
      TestObserver<AsyncPcdmObject> allObjectsObserver = store.objects().test();
      assertThat(allObjectsObserver).observed(object);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      AsyncPcdmObject parent = valueEmittedBy(store.createObject());
      AsyncPcdmObject child = valueEmittedBy(parent.createObject());

      TestObserver<AsyncPcdmObject> memberObjectsObserver = parent.memberObjects().test();
      assertThat(memberObjectsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentObject())).isEqualTo(parent);

      TestObserver<AsyncPcdmObject> allObjectsObserver = store.objects().test();
      assertThat(allObjectsObserver).observed(parent, child);
    }

    @Test
    void createChildFileCreatesAFile() {
      AsyncPcdmObject parent = valueEmittedBy(store.createObject());
      AsyncPcdmFile child = valueEmittedBy(parent.createFile());

      TestObserver<AsyncPcdmFile> memberFilesObserver = parent.memberFiles().test();
      assertThat(memberFilesObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentObject())).isEqualTo(parent);

      TestObserver<AsyncPcdmFile> allFilesObserver = store.files().test();
      assertThat(allFilesObserver).observedExactly(child);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Files {
    @Test
    void filesEmptyByDefault() {
      TestObserver<AsyncPcdmFile> allFilesObserver = store.files().test();
      assertThat(allFilesObserver).observedNothing();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Relations {
    @Test
    void relationsEmptyByDefault() {
      TestObserver<AsyncPcdmRelation> allRelationsObserver = store.relations().test();
      assertThat(allRelationsObserver).observedNothing();
    }

    @Test
    void createRelationCreatesARelation() {
      AsyncPcdmObject fromObject = valueEmittedBy(store.createObject());
      AsyncPcdmObject toObject = valueEmittedBy(store.createObject());

      AsyncPcdmRelation relation = valueEmittedBy(fromObject.relateTo(toObject));
      assertThat(valueEmittedBy(relation.fromObject())).isEqualTo(fromObject);
      assertThat(valueEmittedBy(relation.toObject())).isEqualTo(toObject);

      TestObserver<AsyncPcdmObject> relatedObjectsObserver = fromObject.relatedObjects().test();
      assertThat(relatedObjectsObserver).observedExactly(toObject);

      assertThat(fromObject.outgoingRelations().test()).observedExactly(relation);
      assertThat(toObject.incomingRelations().test()).observedExactly(relation);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Find {
    @Test
    void findFindsAWorkspace() {
      AsyncWorkspace workspace = valueEmittedBy(store.createWorkspace());
      assertThat(store.find(workspace.id()).test()).observedExactly(workspace);
    }

    @Test
    void findFindsACollection() {
      AsyncPcdmCollection collection = valueEmittedBy(store.createCollection());
      assertThat(store.find(collection.id()).test()).observedExactly(collection);
    }

    @Test
    void findFindsAnObject() {
      AsyncPcdmObject object = valueEmittedBy(store.createObject());
      assertThat(store.find(object.id()).test()).observedExactly(object);
    }

    @Test
    void findFindsAFile() {
      AsyncPcdmObject parent = valueEmittedBy(store.createObject());
      AsyncPcdmFile file = valueEmittedBy(parent.createFile());
      assertThat(store.find(file.id()).test()).observedExactly(file);
    }

    @Test
    void findFindsNothing() {
      assertThat(store.find(new UUID(Long.MAX_VALUE, Long.MAX_VALUE)).test()).observedNothing();
    }
  }
}
