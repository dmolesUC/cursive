package org.cdlib.kufi;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceTypeTest {
  @Nested
  class Equals {
    @Test
    void typesAreEqualToThemselves() {
      for (var rt: ResourceType.values()) {
        assertThat(rt).isEqualTo(rt);
      }
    }

    @Test
    void typesAreNotEqualToNull() {
      for (var rt: ResourceType.values()) {
        assertThat(rt).isNotEqualTo(null);
      }
    }
  }
}
