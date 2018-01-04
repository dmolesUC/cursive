package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cdlib.cursive.api.s11n.Cursive.*;

class ResourceSerializationTest {

  @Nested
  class Constructor {
    @Test
    void defaultsToEmpty() {
      assertThat(new ResourceSerialization("/").links()).isEmpty();
    }

    @Test
    void acceptsVarargs() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      ResourceSerialization rs = new ResourceSerialization("/", l1, l2);
      assertThat(rs.links()).containsOnly(l1, l2);
    }
  }

  @Nested
  class WithLink {
    @Test
    void appendsALink() {
      ResourceSerialization rs0 = new ResourceSerialization("/");
      Link link = new Link(WORKSPACES, "workspaces");
      ResourceSerialization rs1 = rs0.withLink(link);
      assertThat(rs1).isNotSameAs(rs0);
      assertThat(rs1.links()).containsOnly(link);
    }

    @Test
    void constructsAndAppendsALinkGivenAStringTarget() {
      ResourceSerialization rs0 = new ResourceSerialization("/");
      ResourceSerialization rs1 = rs0.withLink(WORKSPACES, "workspaces");
      assertThat(rs1).isNotSameAs(rs0);
      Link expected = new Link(WORKSPACES, "workspaces");
      assertThat(rs1.links()).containsOnly(expected);
    }

    @Test
    void constructsAndAppendsALinkGivenAUriTarget() throws URISyntaxException {
      URI target = new URI("workspaces");
      ResourceSerialization rs0 = new ResourceSerialization("/");
      ResourceSerialization rs1 = rs0.withLink(WORKSPACES, target);
      assertThat(rs1).isNotSameAs(rs0);
      Link expected = new Link(WORKSPACES, target);
      assertThat(rs1.links()).containsOnly(expected);
    }
  }

  @Nested
  class AllRelations {
    @Test
    void defaultsToEmpty() {
      assertThat(new ResourceSerialization("/").allRelations()).isEmpty();
    }

    @Test
    void findsAllRelations() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      ResourceSerialization rs = new ResourceSerialization("/", l1, l2);
      assertThat(rs.allRelations()).containsOnly(WORKSPACES, COLLECTIONS);
    }
  }

  @Nested
  class AllNamespaces {
    @Test
    void defaultsToEmpty() {
      assertThat(new ResourceSerialization("/").allNamespaces()).isEmpty();
    }

    @Test
    void findsAllNamespaces() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      ResourceSerialization rs = new ResourceSerialization("/", l1, l2);
      assertThat(rs.allNamespaces()).containsOnly(CURSIVE);
    }
  }

  @Nested
  class Equality {
    @Test
    void equalToSelf() {
      ResourceSerialization rs0 = new ResourceSerialization("/");
      assertThat(rs0).isEqualTo(rs0);
    }

    @Test
    void equalToIdentical() {
      ResourceSerialization rs0 = new ResourceSerialization("/");
      ResourceSerialization rs1 = new ResourceSerialization("/");
      assertThat(rs0).isEqualTo(rs1);
      assertThat(rs1).isEqualTo(rs0);
    }

    @Test
    void equalToSameLinks() {
      ResourceSerialization rs0 = new ResourceSerialization("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      ResourceSerialization rs1 = new ResourceSerialization("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      assertThat(rs0).isEqualTo(rs1);
      assertThat(rs1).isEqualTo(rs0);
    }

    @Test
    void notEqualToDifferentLinks() {
      ResourceSerialization rs0 = new ResourceSerialization("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      ResourceSerialization rs1 = new ResourceSerialization("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(FILES, "files"));

      assertThat(rs0).isNotEqualTo(rs1);
      assertThat(rs1).isNotEqualTo(rs0);
    }

    @Test
    void notEqualToDifferentSelfLink() {
      ResourceSerialization rs0 = new ResourceSerialization("/0")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      ResourceSerialization rs1 = new ResourceSerialization("/1")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      assertThat(rs0).isNotEqualTo(rs1);
      assertThat(rs1).isNotEqualTo(rs0);
    }
  }
}
