package org.cdlib.kufi;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.cdlib.kufi.LinkType.PARENT_OF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkTest {

  private Resource<?> source;
  private Resource<?> target;
  private Transaction tx;
  private Link link;

  @BeforeEach
  void setUp() {
    source = mock(Resource.class);
    target = mock(Resource.class);
    tx = Transaction.initTransaction();
    link = Link.create(source, PARENT_OF, target, tx);
  }

  @Test
  void toStringIncludesDetails() {
    var str = link.toString();
    var fields = List.of(source, link.type(), target, link.createdAt(), link.deletedAt()).map(Object::toString);
    for (var field: fields) {
      assertThat(str).contains(field);
    }
  }

  @Nested
  class Deleted {
    @Test
    void requiresLaterSource() {
      Resource<?> badSource = mock(Resource.class);
      when(badSource.isLaterVersionOf(source)).thenReturn(false);
      Resource<?> goodTarget = mock(Resource.class);
      when(goodTarget.isLaterVersionOf(target)).thenReturn(true);

      assertThatIllegalArgumentException().isThrownBy(() -> link.deleted(badSource, goodTarget, tx.next()))
        .withMessageContaining("Expected source to be later version");
    }

    @Test
    void requiresLaterTarget() {
      Resource<?> goodSource = mock(Resource.class);
      when(goodSource.isLaterVersionOf(source)).thenReturn(true);
      Resource<?> badTarget = mock(Resource.class);
      when(badTarget.isLaterVersionOf(target)).thenReturn(false);

      assertThatIllegalArgumentException().isThrownBy(() -> link.deleted(goodSource, badTarget, tx.next()))
        .withMessageContaining("Expected target to be later version");
    }
  }

  @Nested
  class EqualsAndHashCode {
    @Test
    void linkEqualToItself() {
      assertThat(link).isEqualTo(link);
      assertThat(link.hashCode()).isEqualTo(link.hashCode());
    }

    @Test
    void linkEqualToEqualLink() {
      var link2 = Link.create(source, PARENT_OF, target, tx);
      assertThat(link).isEqualTo(link2);
      assertThat(link.hashCode()).isEqualTo(link2.hashCode());
    }

    @Test
    void linkNotEqualToNull() {
      assertThat(link).isNotEqualTo(null);
    }

    @Test
    void linkWithDifferentSourceIsNotEqual() {
      var link2 = Link.create(mock(Resource.class), link.type(), link.target(), link.createdAt());
      assertThat(link).isNotEqualTo(link2);
      assertThat(link2).isNotEqualTo(link);
    }

    @Test
    void linkWithDifferentTargetIsNotEqual() {
      var link2 = Link.create(link.source(), link.type(), mock(Resource.class), link.createdAt());
      assertThat(link).isNotEqualTo(link2);
      assertThat(link2).isNotEqualTo(link);
    }

    @Test
    void linkWithDifferentTxIsNotEqual() {
      var link2 = Link.create(link.source(), link.type(), link.target(), link.createdAt().next());
      assertThat(link).isNotEqualTo(link2);
      assertThat(link2).isNotEqualTo(link);
    }
  }
}
