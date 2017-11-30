package org.cdlib.cursive;

import io.vavr.collection.Array;

import java.net.URI;
import java.util.Objects;

class Vocab {
  private final String prefix;
  private final URI uri;
  private final Array<String> terms;

  Vocab(String prefix, URI uri, Array<String> terms) {
    Objects.requireNonNull(prefix);
    Objects.requireNonNull(uri);
    Objects.requireNonNull(terms);

    this.prefix = prefix;
    this.uri = uri;
    this.terms = terms;
  }

  @Override
  public String toString() {
    return prefix + ": " + uri + " [" + terms.mkString(", ") + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Vocab vocab = (Vocab) o;

    if (!prefix.equals(vocab.prefix)) {
      return false;
    }
    return uri.equals(vocab.uri);
  }

  @Override
  public int hashCode() {
    int result = prefix.hashCode();
    result = 31 * result + uri.hashCode();
    return result;
  }
}
