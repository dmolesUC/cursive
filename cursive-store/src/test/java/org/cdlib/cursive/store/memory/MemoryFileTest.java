package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.pcdm.PcdmObject;
import org.cdlib.cursive.store.Identifiers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryFileTest {

  private MemoryStore store;

  @BeforeEach
  void setUp() {
    store = new MemoryStore();
  }

  @Test
  void fileMustHaveAParent() {
    assertThatThrownBy(() -> new MemoryFile(null, new UUID(Long.MAX_VALUE, Long.MAX_VALUE)))
      .isInstanceOf(NullPointerException.class)
      .withFailMessage("%s must have a parent", MemoryFile.class.getSimpleName());
  }

  @Test
  void fileMustHaveAnIdentifier() {
    PcdmObject parent = store.createObject();
    assertThatThrownBy(() -> new MemoryFile(parent, null))
      .isInstanceOf(NullPointerException.class)
      .withFailMessage("%s must have an identifier", MemoryFile.class.getSimpleName());
  }

  @Test
  void constructorSetsIdentifier() {
    UUID identifier = Identifiers.mintIdentifier();
    PcdmObject parent = store.createObject();
    MemoryFile file = new MemoryFile(parent, identifier);
    assertThat(file.id()).isEqualTo(identifier);
  }

  @Test
  void filesWithSameIdentifierAreEqual() {
    UUID identifier = Identifiers.mintIdentifier();
    PcdmObject parent = store.createObject();
    MemoryFile file1 = new MemoryFile(parent, identifier);
    MemoryFile file2 = new MemoryFile(parent, identifier);
    assertThat(file1).isEqualTo(file2);
    assertThat(file2).isEqualTo(file1);
    assertThat(file1.hashCode()).isEqualTo(file2.hashCode());
    assertThat(file2.hashCode()).isEqualTo(file1.hashCode());
  }

  @Test
  void toStringIncludesTypeAndIdentifier() {
    UUID identifier = Identifiers.mintIdentifier();
    PcdmObject parent = store.createObject();
    MemoryFile file = new MemoryFile(parent, identifier);
    assertThat(file.toString())
      .contains(MemoryFile.class.getSimpleName())
      .contains(identifier.toString());
  }
}
