package org.cdlib.cursive.api.s11n;

import org.cdlib.cursive.util.Strings;

import java.net.URI;

public class Namespace {

  private final String prefix;
  private final URI uriBase;

  public Namespace(String prefix, String uriBase) {
    Strings.requireNotBlank(prefix);
    Strings.requireNotBlank(uriBase);

    this.prefix = prefix;
    this.uriBase = URI.create(uriBase);
  }

  public String getPrefix() {
    return prefix;
  }

  public URI getUriBase() {
    return uriBase;
  }

  public String prefix(String term) {
    return prefix + ":" + term;
  }

  @Override
  public String toString() {
    return "Namespace(" + prefix + ", " + uriBase + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Namespace namespace = (Namespace) o;

    if (!prefix.equals(namespace.prefix)) {
      return false;
    }
    return uriBase.equals(namespace.uriBase);
  }

  @Override
  public int hashCode() {
    int result = prefix.hashCode();
    result = 31 * result + uriBase.hashCode();
    return result;
  }
}
