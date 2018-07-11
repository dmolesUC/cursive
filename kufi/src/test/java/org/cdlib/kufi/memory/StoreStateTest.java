package org.cdlib.kufi.memory;

import org.cdlib.kufi.Resource;
import org.cdlib.kufi.ResourceNotFoundException;
import org.cdlib.kufi.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StoreStateTest {

  private StoreState state;

  @BeforeEach
  void setUp() {
    state = new StoreState();
  }

  @ParameterizedTest
  @MethodSource("org.cdlib.kufi.ResourceType#values")
  @SuppressWarnings("unchecked")
  void deleteFailsForNonexistentResource(ResourceType<?> type) {
    var r = mock(Resource.class);
    when(r.id()).thenReturn(UUID.randomUUID());
    when(r.type()).thenReturn(type);

    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> state.deleteRecursive(r))
      .withMessageContaining(r.id().toString())
      .withMessageContaining(r.type().toString())
      .matches(e -> e.id().equals(r.id()))
      .matches(e -> e.type().equals(r.type()))
    ;
  }
}