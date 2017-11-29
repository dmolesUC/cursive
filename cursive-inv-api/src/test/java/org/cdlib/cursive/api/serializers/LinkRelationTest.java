package org.cdlib.cursive.api.serializers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LinkRelationTest {
  @Test
  void validNamespaceIsValid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    LinkRelation rel = new LinkRelation(ns, "workspaces");
    assertThat(rel.getNamespace()).isEqualTo(ns);
  }

  @Test
  void nullNamespaceIsInvalid() {
    assertThatThrownBy(() -> new LinkRelation(null, "workspaces"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void validTermIsValid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    LinkRelation rel = new LinkRelation(ns, "workspaces");
    assertThat(rel.getTerm()).isEqualTo("workspaces");
    assertThat(rel.getUri())
      .isNotNull()
      .hasScheme("https")
      .hasHost("github.com")
      .hasPath("/dmolesUC3/cursive/blob/master/RELATIONS.md")
      .hasFragment("workspaces");
    assertThat(rel.getPrefixedForm()).isEqualTo("cursive:workspaces");
  }

  @Test
  void nullTermIsInvalid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    assertThatThrownBy(() -> new LinkRelation(ns, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void blankTermIsInvalid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    assertThatThrownBy(() -> new LinkRelation(ns, ""))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void pathBasedUriBaseIsValid() {
    Namespace ns = new Namespace("foaf", "http://xmlns.com/foaf/0.1/");
    LinkRelation rel = new LinkRelation(ns, "name");
    assertThat(rel.getUri())
      .isNotNull()
      .hasScheme("http")
      .hasHost("xmlns.com")
      .hasPath("/foaf/0.1/name")
    ;
    assertThat(rel.getPrefixedForm()).isEqualTo("foaf:name");
  }

}
