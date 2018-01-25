package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NamespaceTest {
  @Test
  void validUriBaseIsValid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    assertThat(ns.getUriBase())
      .isNotNull()
      .hasScheme("https")
      .hasHost("github.com")
      .hasPath("/dmolesUC3/cursive/blob/master/RELATIONS.md")
      .hasFragment("")
    ;
  }

  @Test
  void pathBasedUriBaseIsValid() {
    Namespace ns = new Namespace("foaf", "http://xmlns.com/foaf/0.1/");
    assertThat(ns.getUriBase())
      .isNotNull()
      .hasScheme("http")
      .hasHost("xmlns.com")
      .hasPath("/foaf/0.1/")
    ;
  }

  @Test
  void nullUriBaseIsInvalid() {
    assertThatThrownBy(() -> new Namespace("cursive", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void blankUriBaseIsInvalid() {
    assertThatThrownBy(() -> new Namespace("cursive", ""))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void validPrefixIsValid() {
    Namespace ns = new Namespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    assertThat(ns.getPrefix()).isEqualTo("cursive");
  }

  @Test
  void nullPrefixIsInvalid() {
    assertThatThrownBy(() -> new Namespace(null, "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void blankPrefixIsInvalid() {
    assertThatThrownBy(() -> new Namespace("", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#"))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
