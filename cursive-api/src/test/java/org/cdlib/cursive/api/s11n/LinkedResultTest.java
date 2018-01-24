package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cdlib.cursive.api.s11n.Cursive.*;

class LinkedResultTest {

  @Nested
  class Constructor {
    @Test
    void defaultsToEmpty() {
      assertThat(new LinkedResult("/").links()).isEmpty();
    }

    @Test
    void acceptsVarargs() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      LinkedResult res = new LinkedResult("/", l1, l2);
      assertThat(res.links()).containsOnly(l1, l2);
    }
  }

  @Nested
  class WithLink {
    @Test
    void appendsALink() {
      LinkedResult res0 = new LinkedResult("/");
      Link link = new Link(WORKSPACES, "workspaces");
      LinkedResult res1 = res0.withLink(link);
      assertThat(res1).isNotSameAs(res0);
      assertThat(res1.links()).containsOnly(link);
    }

    @Test
    void constructsAndAppendsALinkGivenAStringTarget() {
      LinkedResult res0 = new LinkedResult("/");
      LinkedResult res1 = res0.withLink(WORKSPACES, "workspaces");
      assertThat(res1).isNotSameAs(res0);
      Link expected = new Link(WORKSPACES, "workspaces");
      assertThat(res1.links()).containsOnly(expected);
    }

    @Test
    void constructsAndAppendsALinkGivenAUriTarget() throws URISyntaxException {
      URI target = new URI("workspaces");
      LinkedResult res0 = new LinkedResult("/");
      LinkedResult res1 = res0.withLink(WORKSPACES, target);
      assertThat(res1).isNotSameAs(res0);
      Link expected = new Link(WORKSPACES, target);
      assertThat(res1.links()).containsOnly(expected);
    }
  }

  @Nested
  class AllRelations {
    @Test
    void defaultsToEmpty() {
      assertThat(new LinkedResult("/").allRelations()).isEmpty();
    }

    @Test
    void findsAllRelations() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      LinkedResult res = new LinkedResult("/", l1, l2);
      assertThat(res.allRelations()).containsOnly(WORKSPACES, COLLECTIONS);
    }
  }

  @Nested
  class AllNamespaces {
    @Test
    void defaultsToEmpty() {
      assertThat(new LinkedResult("/").allNamespaces()).isEmpty();
    }

    @Test
    void findsAllNamespaces() {
      Link l1 = new Link(WORKSPACES, "workspaces");
      Link l2 = new Link(COLLECTIONS, "collections");
      LinkedResult res = new LinkedResult("/", l1, l2);
      assertThat(res.allNamespaces()).containsOnly(CURSIVE);
    }
  }

  @Nested
  class Equality {
    @Test
    void equalToSelf() {
      LinkedResult res0 = new LinkedResult("/");
      assertThat(res0).isEqualTo(res0);
    }

    @Test
    void equalToIdentical() {
      LinkedResult res0 = new LinkedResult("/");
      LinkedResult res1 = new LinkedResult("/");
      assertThat(res0).isEqualTo(res1);
      assertThat(res1).isEqualTo(res0);
    }

    @Test
    void equalToSameLinks() {
      LinkedResult res0 = new LinkedResult("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      LinkedResult res1 = new LinkedResult("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      assertThat(res0).isEqualTo(res1);
      assertThat(res1).isEqualTo(res0);
    }

    @Test
    void notEqualToDifferentLinks() {
      LinkedResult res0 = new LinkedResult("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      LinkedResult res1 = new LinkedResult("/")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(FILES, "files"));

      assertThat(res0).isNotEqualTo(res1);
      assertThat(res1).isNotEqualTo(res0);
    }

    @Test
    void notEqualToDifferentSelfLink() {
      LinkedResult res0 = new LinkedResult("/0")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      LinkedResult res1 = new LinkedResult("/1")
        .withLink(new Link(WORKSPACES, "workspaces"))
        .withLink(new Link(OBJECTS, "objects"));

      assertThat(res0).isNotEqualTo(res1);
      assertThat(res1).isNotEqualTo(res0);
    }
  }
}
