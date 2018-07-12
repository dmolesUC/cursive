package org.cdlib.kufi.memory;

import org.cdlib.kufi.AbstractStoreTest;
import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static io.vavr.control.Option.none;
import static org.cdlib.cursive.util.RxAssertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoryStoreTest extends AbstractStoreTest<MemoryStore> {
  @Override
  protected MemoryStore newStore() {
    return new MemoryStore();
  }

  @Nested
  class ErrorHandling {

    private StoreState state;
    private MemoryStore store;

    @BeforeEach
    void setUp() {
      state = mock(StoreState.class);
      store = new MemoryStore(state);
    }

    @Test
    void createWorkspaceWrapsErrors() {
      var error = new RuntimeException();
      when(state.createWorkspace(any())).thenThrow(error);
      assertThat(store.createWorkspace()).emittedError(error);
    }

    @Test
    void findWrapsErrors() {
      var error = new RuntimeException();
      when(state.find(any())).thenThrow(error);
      assertThat(store.find(UUID.randomUUID())).emittedError(error);
    }

    @Test
    void findWithTypeWrapsErrors() {
      var error = new RuntimeException();
      when(state.find(any())).thenThrow(error);
      assertThat(store.find(UUID.randomUUID(), ResourceType.WORKSPACE)).emittedError(error);
    }

    @Test
    void findTombstoneWrapsErrors() {
      var error = new RuntimeException();
      when(state.findTombstone(any())).thenThrow(error);
      assertThat(store.findTombstone(UUID.randomUUID())).emittedError(error);
    }

    @Test
    void findTombstoneWithTypeWrapsErrors() {
      var error = new RuntimeException();
      when(state.findTombstone(any())).thenThrow(error);
      assertThat(store.findTombstone(UUID.randomUUID(), ResourceType.WORKSPACE)).emittedError(error);
    }

    @Test
    void findParentForOrphanEmitsNoSuchElementException() {
      when(state.findParent(any())).thenReturn(none());
      assertThat(store.findParentOf(mock(Resource.class))).emittedErrorOfType(NoSuchElementException.class);
    }
  }
}
