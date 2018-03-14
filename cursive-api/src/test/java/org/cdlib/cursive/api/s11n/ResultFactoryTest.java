package org.cdlib.cursive.api.s11n;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.cdlib.cursive.core.async.AsyncStore;
import org.cdlib.cursive.pcdm.async.AsyncPcdmCollection;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.store.async.adapters.AsyncStoreAdapter;
import org.cdlib.cursive.store.memory.MemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cdlib.cursive.api.s11n.Pcdm.*;
import static org.cdlib.cursive.util.RxAssertions.valueEmittedBy;

class ResultFactoryTest {

  private AsyncStore store;
  private ResultFactory factory;

  @BeforeEach
  void setUp() {
    store = AsyncStoreAdapter.toAsync(new MemoryStore());
    factory = new ResultFactory();
  }

  @Nested
  class File {
    AsyncPcdmObject parent;
    AsyncPcdmFile file;
    LinkedResult result;

    @BeforeEach
    void setUp() {
      parent = valueEmittedBy(store.createObject());
      file = valueEmittedBy(parent.createFile());
      result = valueEmittedBy(factory.toResult(file));
    }

    @Test
    void createsFileResult() {
      assertThat(result).isNotNull();
    }

    @Test
    void includesSelfLink() {
      URI expected = URI.create(file.path());
      assertThat(result.selfPath()).isEqualTo(expected);
    }

    @Test
    void includesParentLink() {
      URI expected = URI.create(parent.path());
      Option<Link> parentLink = result.links().find(l -> FILE_OF.equals(l.rel()));
      assertThat(parentLink).isNotEmpty();
      parentLink.forEach(l -> assertThat(l.target()).isEqualTo(expected));
    }

    @Test
    void includesNoOtherLinks() {
      assertThat(result.links()).hasSize(1);
    }
  }

  @Nested
  class Object {

    abstract class ObjectTest {
      AsyncPcdmObject object;
      LinkedResult result;

      @Test
      void createsObjectResult() {
        assertThat(result).isNotNull();
      }

      @Test
      void includesSelfLink() {
        URI expected = URI.create(object.path());
        assertThat(result.selfPath()).isEqualTo(expected);
      }

    }

    @Nested
    class TopLevel extends ObjectTest {

      @BeforeEach
      void setUp() {
        object = valueEmittedBy(store.createObject());
        result = valueEmittedBy(factory.toResult(object));
      }

      @Test
      void includesNoOtherLinks() {
        assertThat(result.links()).isEmpty();
      }
    }

    @Nested
    class CollectionParent extends ObjectTest {
      AsyncPcdmCollection parent;

      @BeforeEach
      void setUp() {
        parent = valueEmittedBy(store.createCollection());
        object = valueEmittedBy(parent.createObject());
        result = valueEmittedBy(factory.toResult(object));
      }

      @Test
      void includesParentLink() {
        URI expected = URI.create(parent.path());
        Option<Link> parentLink = result.links().find(l -> MEMBER_OF.equals(l.rel()));
        assertThat(parentLink).isNotEmpty();
        parentLink.forEach(l -> assertThat(l.target()).isEqualTo(expected));
      }
    }

    @Nested
    class ObjectParent extends ObjectTest {
      AsyncPcdmObject parent;

      @BeforeEach
      void setUp() {
        parent = valueEmittedBy(store.createObject());
        object = valueEmittedBy(parent.createObject());
        result = valueEmittedBy(factory.toResult(object));
      }

      @Test
      void includesParentLink() {
        assertThat(valueEmittedBy(object.parent())).isEqualTo(parent); // just to be sure

        Link expected = new Link(MEMBER_OF, URI.create(parent.path()));
        Set<Link> links = result.links();
        assertThat(links).contains(expected);
      }
    }

    @Nested
    class WithChildren extends ObjectTest {
      List<AsyncPcdmObject> childObjects;
      List<AsyncPcdmFile> childFiles;

      @BeforeEach
      void setUp() {
        object = valueEmittedBy(store.createObject());
        childObjects = List.fill(3, () -> valueEmittedBy(object.createObject()));
        childFiles = List.fill(3, () -> valueEmittedBy(object.createFile()));
        result = valueEmittedBy(factory.toResult(object));
      }

      @Test
      void includesChildObjects() {
        Set<Link> links = result.links();
        for (AsyncPcdmObject childObject : childObjects) {
          assertThat(links).contains(new Link(HAS_MEMBER, childObject.path()));
        }
      }

      @Test
      void includesChildFiles() {
        Set<Link> links = result.links();
        for (AsyncPcdmFile childFile : childFiles) {
          assertThat(links).contains(new Link(HAS_FILE, childFile.path()));
        }
      }
    }
  }

  @Nested
  class Collection {
  }

  @Nested
  class Workspace {
  }

  @Nested
  class Store {

  }
}
