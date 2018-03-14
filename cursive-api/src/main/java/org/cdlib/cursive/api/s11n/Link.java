package org.cdlib.cursive.api.s11n;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

class Link {

  private final LinkRelation rel;
  private final URI target;

  public Link(LinkRelation rel, String target) {
    this(rel, toURI(target));
  }

  public Link(LinkRelation rel, URI target) {
    Objects.requireNonNull(rel);
    Objects.requireNonNull(target);
    this.rel = rel;
    this.target = target;
  }

  public LinkRelation rel() {
    return rel;
  }

  public URI target() {
    return target;
  }

  private static URI toURI(String target) {
    try {
      return new URI(target);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public String toString() {
    return rel + " -> " + target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Link link = (Link) o;

    if (!rel.equals(link.rel)) {
      return false;
    }
    return target.equals(link.target);
  }

  @Override
  public int hashCode() {
    int result = rel.hashCode();
    result = 31 * result + target.hashCode();
    return result;
  }
}
