package org.cdlib.cursive;

import com.squareup.javapoet.TypeSpec;
import io.vavr.collection.Array;

import java.net.URI;
import java.util.Objects;

class Vocab implements Comparable<Vocab> {
  private final String prefix;
  private final URI uri;
  private final Array<String> terms;
  private final String constName;

  Vocab(String prefix, URI uri, Array<String> terms) {
    Objects.requireNonNull(prefix);
    Objects.requireNonNull(uri);
    Objects.requireNonNull(terms);

    this.prefix = prefix;
    this.constName = toConstName(prefix);
    this.uri = uri;
    this.terms = terms;
  }

  static String toConstName(String rValue) {
    return rValue
      .replaceAll("/+$", "")
      .replaceAll("([a-z])([A-Z])", "$1_$2")
      .toUpperCase()
      .replaceAll("^([^_A-Z])", "_$1")
      .replaceAll("[^_$A-Z0-9]", "_");
  }

  public String getPrefix() {
    return prefix;
  }

  public URI getUri() {
    return uri;
  }

  public Array<String> getTerms() {
    return terms;
  }

  public String getConstName() {
    return constName;
  }

  TypeSpec.Builder addVocabEnumConstant(TypeSpec.Builder vocabsBuilder) {
    return vocabsBuilder.addEnumConstant(
      constName,
      TypeSpec.anonymousClassBuilder("$S, new URI($S)", prefix, uri.toString()).build()
    );
  }

  @Override
  public String toString() {
    String termList = terms.map(t -> String.format("%s (%s)", t, toConstName(t))).mkString(", ");
    return String.format("%s (%s): %s [%s]", prefix, constName, uri, termList);
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

  @Override
  public int compareTo(Vocab o) {
    Objects.requireNonNull(o);
    if (o == this) {
      return 0;
    }

    int order = prefix.compareTo(o.prefix);
    if (order == 0) {
      throw new IllegalStateException(String.format("Prefix \"%s\" shared by two vocabularies: <%s>, <%s>", prefix, this, o));
    }

    return order;
  }
}
