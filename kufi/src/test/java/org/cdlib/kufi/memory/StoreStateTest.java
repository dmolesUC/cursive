package org.cdlib.kufi.memory;

import org.cdlib.kufi.ResourceNotFoundException;
import org.cdlib.kufi.ResourceType;
import org.cdlib.kufi.Transaction;
import org.cdlib.kufi.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static io.vavr.control.Option.none;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StoreStateTest {

  private StoreState state;

  @BeforeEach
  void setUp() {
    state = new StoreState();
  }

  @ParameterizedTest
  @MethodSource("org.cdlib.kufi.ResourceType#values") // TODO: fix test
  @SuppressWarnings("unchecked")
  void deleteFailsForNonexistentResource(ResourceType<?> type) {
    var r = new MemoryResource(type, UUID.randomUUID(), Version.initVersion(Transaction.initTransaction()), none(), new MemoryStore(state)) {};
    assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> state.delete(r, true))
      .withMessageContaining(r.id().toString())
      .withMessageContaining(r.type().toString())
      .matches(e -> e.id().equals(r.id()))
      .matches(e -> e.type().equals(r.type()))
    ;
  }
}
