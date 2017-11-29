package org.cdlib.cursive.api.serializers;

import org.cdlib.cursive.util.Strings;

import java.net.URI;
import java.util.Objects;

public class LinkRelation {
  private final Namespace namespace;
  private final String term;
  private final URI uri;
  private final String prefixedForm;

  public LinkRelation(Namespace namespace, String term) {
    Objects.requireNonNull(namespace);
    Strings.requireNotBlank(term);
    this.namespace = namespace;
    this.term = term;
    this.uri = URI.create(namespace.getUriBase() + term);
    this.prefixedForm = namespace.getPrefix() + ":" + term;
  }

  public Namespace getNamespace() {
    return namespace;
  }

  public String getTerm() {
    return term;
  }

  public URI getUri() {
    return uri;
  }

  public String getPrefixedForm() {
    return prefixedForm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LinkRelation linkRelation = (LinkRelation) o;

    if (!namespace.equals(linkRelation.namespace)) {
      return false;
    }
    return term.equals(linkRelation.term);
  }

  @Override
  public int hashCode() {
    int result = namespace.hashCode();
    result = 31 * result + term.hashCode();
    return result;
  }
}
