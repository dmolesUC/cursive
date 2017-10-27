package org.cdlib.cursive.store.rx;

import io.reactivex.observers.TestObserver;
import io.vavr.collection.List;
import org.cdlib.cursive.core.rx.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.cdlib.cursive.util.RxJavaAssertions.valueEmittedBy;
import static org.cdlib.cursive.util.RxJavaAssertions.valuesEmittedBy;
import static org.cdlib.cursive.util.RxJavaAssertions.assertThat;

public abstract class AbstractRxStoreTest<S extends RxStore> {
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
      TestObserver<RxCWorkspace> workspacesObserver = store.workspaces().test();
      assertThat(workspacesObserver).isComplete();
      assertThat(workspacesObserver).observedNothing();
      assertThat(workspacesObserver).observedNoErrors();
    }

    @Test
    void createWorkspaceCreatesAWorkspace() {
      RxCWorkspace workspace = valueEmittedBy(store.createWorkspace());
      TestObserver<RxCWorkspace> allWorkspacesObserver = store.workspaces().test();
      assertThat(allWorkspacesObserver).observedExactly(workspace);
    }

    @Test
    void newWorkspaceIsEmpty() {
      RxCWorkspace workspace = valueEmittedBy(store.createWorkspace());
      TestObserver<RxCCollection> memberCollectionsObserver = workspace.memberCollections().test();
      assertThat(memberCollectionsObserver).observedNothing();
    }

    @Test
    void createChildCollectionCreatesACollection() {
      RxCWorkspace workspace = valueEmittedBy(store.createWorkspace());
      RxCCollection collection = valueEmittedBy(workspace.createCollection());

      TestObserver<RxCCollection> memberCollectionsObserver = workspace.memberCollections().test();
      assertThat(memberCollectionsObserver).observedExactly(collection);

      assertThat(valueEmittedBy(collection.parentWorkspace())).isEqualTo(workspace);

      TestObserver<RxCCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observedExactly(collection);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Collections {
    @Test
    void collectionsEmptyByDefault() {
      List<RxCCollection> collections = valuesEmittedBy(store.collections());
      assertThat(collections).isEmpty();
    }

    @Test
    void createCollectionCreatesACollection() {
      RxCCollection collection = valueEmittedBy(store.createCollection());
      TestObserver<RxCCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observedExactly(collection);
    }

    @Test
    void createChildCollectionCreatesACollection() {
      RxCCollection parent = valueEmittedBy(store.createCollection());
      RxCCollection child = valueEmittedBy(parent.createCollection());

      TestObserver<RxCCollection> memberCollectionsObserver = parent.memberCollections().test();
      assertThat(memberCollectionsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentCollection())).isEqualTo(parent);

      TestObserver<RxCCollection> allCollectionsObserver = store.collections().test();
      assertThat(allCollectionsObserver).observed(parent, child);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      RxCCollection parent = valueEmittedBy(store.createCollection());
      RxCObject child = valueEmittedBy(parent.createObject());

      TestObserver<RxCObject> memberObjectsObserver = parent.memberObjects().test();
      assertThat(memberObjectsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentCollection())).isEqualTo(parent);

      TestObserver<RxCObject> allObjectsObserver = store.objects().test();
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
      RxCObject object = valueEmittedBy(store.createObject());
      TestObserver<RxCObject> allObjectsObserver = store.objects().test();
      assertThat(allObjectsObserver).observed(object);
    }

    @Test
    void createChildObjectCreatesAnObject() {
      RxCObject parent = valueEmittedBy(store.createObject());
      RxCObject child = valueEmittedBy(parent.createObject());

      TestObserver<RxCObject> memberObjectsObserver = parent.memberObjects().test();
      assertThat(memberObjectsObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentObject())).isEqualTo(parent);

      TestObserver<RxCObject> allObjectsObserver = store.objects().test();
      assertThat(allObjectsObserver).observed(parent, child);
    }

    @Test
    void createChildFileCreatesAFile() {
      RxCObject parent = valueEmittedBy(store.createObject());
      RxCFile child = valueEmittedBy(parent.createFile());

      TestObserver<RxCFile> memberFilesObserver = parent.memberFiles().test();
      assertThat(memberFilesObserver).observedExactly(child);

      assertThat(valueEmittedBy(child.parentObject())).isEqualTo(parent);

      TestObserver<RxCFile> allFilesObserver = store.files().test();
      assertThat(allFilesObserver).observedExactly(child);
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Files {
    @Test
    void filesEmptyByDefault() {
      TestObserver<RxCFile> allFilesObserver = store.files().test();
      assertThat(allFilesObserver).observedNothing();
    }
  }

  @Nested
  @SuppressWarnings("unused")
  class Relations {
    @Test
    void relationsEmptyByDefault() {
      TestObserver<RxCRelation> allRelationsObserver = store.relations().test();
      assertThat(allRelationsObserver).observedNothing();
    }

    @Test
    void createRelationCreatesARelation() {
      RxCObject fromObject = valueEmittedBy(store.createObject());
      RxCObject toObject = valueEmittedBy(store.createObject());

      RxCRelation relation = valueEmittedBy(fromObject.relateTo(toObject));
      assertThat(valueEmittedBy(relation.fromObject())).isEqualTo(fromObject);
      assertThat(valueEmittedBy(relation.toObject())).isEqualTo(toObject);

      TestObserver<RxCObject> relatedObjectsObserver = fromObject.relatedObjects().test();
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
      RxCWorkspace workspace = valueEmittedBy(store.createWorkspace());
      assertThat(store.find(workspace.identifier()).test()).observedExactly(workspace);
    }

    @Test
    void findFindsACollection() {
      RxCCollection collection = valueEmittedBy(store.createCollection());
      assertThat(store.find(collection.identifier()).test()).observedExactly(collection);
    }

    @Test
    void findFindsAnObject() {
      RxCObject object = valueEmittedBy(store.createObject());
      assertThat(store.find(object.identifier()).test()).observedExactly(object);
    }

    @Test
    void findFindsAFile() {
      RxCObject parent = valueEmittedBy(store.createObject());
      RxCFile file = valueEmittedBy(parent.createFile());
      assertThat(store.find(file.identifier()).test()).observedExactly(file);
    }

    @Test
    void findFindsNothing() {
      assertThat(store.find("I am not a valid identifier").test()).observedNothing();
    }
  }}
