package org.cdlib.kufi;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.cdlib.kufi.ResourceType.COLLECTION;
import static org.cdlib.kufi.ResourceType.WORKSPACE;

class AbstractResourceTest {

  // ------------------------------------------------------------
  // Tests

  @Test
  void typeChecksDoNotDependOnImplClass() {
    var id = UUID.randomUUID();
    var tx = Transaction.initTransaction();
    var v = Version.initVersion(tx);
    var r1 = new MockResource<>(COLLECTION, id, v, none());
    var r2 = new MockResource<>(WORKSPACE, id, v, none());
    assertThat(r1).isNotEqualTo(r2);
    assertThat(r2).isNotEqualTo(r1);
  }

  @Test
  void deletedAtVersionMustBeCurrent() {
    var tx = Transaction.initTransaction();
    var v0 = Version.initVersion(tx);
    var v1 = v0.next(tx.next());
    var id = UUID.randomUUID();
    var type = COLLECTION;
    assertThatIllegalArgumentException().isThrownBy(() -> new MockResource<>(type, id, v0, some(v1)))
      .withMessageContaining(type.toString())
      .withMessageContaining(id.toString())
      .withMessageContaining(v0.toString())
      .withMessageContaining(v1.toString())
    ;
  }

  // ------------------------------------------------------------
  // Helper classes

  static class MockResource<R extends Resource<R>> extends AbstractResource<R> {
    private MockResource(ResourceType<R> type, UUID id, Version currentVersion, Option<Version> deletedAt) {
      super(type, id, currentVersion, deletedAt);
    }

  }

}
