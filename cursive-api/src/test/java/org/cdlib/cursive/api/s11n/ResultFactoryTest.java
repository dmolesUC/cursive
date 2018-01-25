package org.cdlib.cursive.api.s11n;

import io.vavr.control.Option;
import org.cdlib.cursive.core.async.AsyncStore;
import org.cdlib.cursive.pcdm.async.AsyncPcdmFile;
import org.cdlib.cursive.pcdm.async.AsyncPcdmObject;
import org.cdlib.cursive.store.async.adapters.AsyncStoreAdapter;
import org.cdlib.cursive.store.memory.MemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
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
    AsyncPcdmObject parentObj;
    AsyncPcdmFile file;

    @BeforeEach
    void setUp() {
      parentObj = valueEmittedBy(store.createObject());
      file = valueEmittedBy(parentObj.createFile());
    }

    @Test
    void createsFileResult() {
      LinkedResult result = valueEmittedBy(factory.toResult(file));
      assertThat(result).isNotNull();
    }

    @Test
    void includesSelfLink() {
      // TODO: figure out how resource URIs are created & who's responsible
      URI expected = URI.create("/path/to/some/file");
      LinkedResult result = valueEmittedBy(factory.toResult(file));
      assertThat(result.selfPath()).isEqualTo(expected);
    }

    @Test
    void includesParentLink() {
      // TODO: figure out how resource URIs are created & who's responsible
      URI expected = URI.create("/path/to/some/object");
      LinkedResult result = valueEmittedBy(factory.toResult(file));
      Option<Link> parentLink = result.links().find(l -> Pcdm.FILE_OF.equals(l.rel()));
      assertThat(parentLink).isNotEmpty();
      parentLink.forEach(l -> assertThat(l.target()).isEqualTo(expected));
    }
  }

  @Nested
  class Object {

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
