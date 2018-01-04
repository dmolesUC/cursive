package org.cdlib.cursive.api.s11n;

import io.vavr.Lazy;
import io.vavr.Value;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.Set;

import java.net.URI;

public class ResourceSerialization {

  // ------------------------------------------------------------
  // Fields

  private final String selfPath;
  private final Set<Link> links;
  private final Lazy<Set<LinkRelation>> allRelations = Lazy.of(this::findAllRelations);
  private final Lazy<Set<Namespace>> allNamespaces = Lazy.of(this::findAllNamespaces);

  // ------------------------------------------------------------
  // Constructors

  public ResourceSerialization(String selfPath) {
    this(selfPath, LinkedHashSet.empty());
  }

  public ResourceSerialization(String selfPath, Link... links) {
    this(selfPath, Array.of(links));
  }

  public ResourceSerialization(String selfPath, Value<Link> links) {
    this(selfPath, links.toLinkedSet());
  }

  public ResourceSerialization(String selfPath, Set<Link> links) {
    this.selfPath = selfPath;
    this.links = links.toLinkedSet();
  }

  // ------------------------------------------------------------
  // Builders

  public ResourceSerialization withLink(Link link) {
    return new ResourceSerialization(selfPath, links.add(link));
  }

  public ResourceSerialization withLink(LinkRelation rel, String target) {
    return withLink(new Link(rel, target));
  }

  public ResourceSerialization withLink(LinkRelation rel, URI target) {
    return withLink(new Link(rel, target));
  }

  // ------------------------------------------------------------
  // Accessors

  public String selfPath() {
    return selfPath;
  }

  public Set<Link> links() {
    return links;
  }

  public Set<LinkRelation> allRelations() {
    return allRelations.get();
  }

  public Set<Namespace> allNamespaces() {
    return allNamespaces.get();
  }

  // ------------------------------------------------------------
  // Private methods

  private Set<LinkRelation> findAllRelations() {
    return links().map(Link::rel).toLinkedSet();
  }

  private Set<Namespace> findAllNamespaces() {
    return allRelations().map(LinkRelation::namespace).toSet();
  }

  // ------------------------------------------------------------
  // Misc

  @Override
  public String toString() {
    return "ResourceSerialization(" + selfPath + ", " + links.mkString("(", ", ", ")") + ")";
  }


  // ------------------------------------------------------------
  // Equality

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResourceSerialization that = (ResourceSerialization) o;

    if (!selfPath.equals(that.selfPath)) {
      return false;
    }
    return links.equals(that.links);
  }

  @Override
  public int hashCode() {
    int result = selfPath.hashCode();
    result = 31 * result + links.hashCode();
    return result;
  }
}
