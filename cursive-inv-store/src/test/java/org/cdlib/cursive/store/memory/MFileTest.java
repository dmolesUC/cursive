package org.cdlib.cursive.store.memory;

import org.cdlib.cursive.core.CObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MFileTest {

  private MemoryStore store;

  @BeforeEach
  public void setUp() {
    store = new MemoryStore();
  }

  @Test
  public void fileMustHaveAParent() {
    assertThatThrownBy(() -> new MFile(null, "Elvis"))
      .isInstanceOf(NullPointerException.class)
      .withFailMessage("%s must have a parent", MFile.class.getSimpleName());
  }

  @Test
  public void fileMustHaveAnIdentifier() {
    CObject parent = store.createObject();
    assertThatThrownBy(() -> new MFile(parent, null))
      .isInstanceOf(NullPointerException.class)
      .withFailMessage("%s must have an identifier", MFile.class.getSimpleName());
  }

  @Test
  public void constructorSetsIdentifier() {
    String identifier = "Elvis";
    CObject parent = store.createObject();
    MFile file = new MFile(parent, identifier);
    assertThat(file.identifier()).isEqualTo(identifier);
  }

  @Test
  public void filesWithSameIdentifierAreEqual() {
    String identifier = "Elvis";
    CObject parent = store.createObject();
    MFile file1 = new MFile(parent, identifier);
    MFile file2 = new MFile(parent, identifier);
    assertThat(file1).isEqualTo(file2);
    assertThat(file2).isEqualTo(file1);
    assertThat(file1.hashCode()).isEqualTo(file2.hashCode());
    assertThat(file2.hashCode()).isEqualTo(file1.hashCode());
  }

  @Test
  public void toStringIncludesTypeAndIdentifier() {
    String identifier = "Elvis";
    CObject parent = store.createObject();
    MFile file = new MFile(parent, identifier);
    assertThat(file.toString())
      .contains(MFile.class.getSimpleName())
      .contains(identifier);
  }
}
