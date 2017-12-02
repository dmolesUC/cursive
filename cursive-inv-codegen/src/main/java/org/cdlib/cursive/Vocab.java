package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.Lazy;
import io.vavr.collection.Array;
import org.cdlib.cursive.util.Strings;

import javax.lang.model.element.Modifier;
import java.net.URI;
import java.util.Objects;

import static org.cdlib.cursive.Generators.CURSIVE_RTF_PACKAGE;

class Vocab implements Comparable<Vocab> {
  private final String prefix;
  private final URI uri;
  private final Array<String> terms;
  private final String constName;
  private final String className;
  private final String rubyClassName;

  Vocab(String rubyClassName, String prefix, URI uri, Array<String> terms) {
    Objects.requireNonNull(rubyClassName);
    Objects.requireNonNull(prefix);
    Objects.requireNonNull(uri);
    Objects.requireNonNull(terms);

    this.rubyClassName = rubyClassName;
    this.prefix = prefix;
    this.constName = toConstName(rubyClassName);
    this.uri = uri;
    this.terms = terms.sortBy(Vocab::toConstName);
    className = toClassName(rubyClassName);
  }

  static String toConstName(String rValue) {
    return rValue
      .replaceAll("/+$", "")
      .replaceAll("([a-z])([A-Z])", "$1_$2")
      .replaceAll("([0-9])([A-Z])", "$1_$2")
      .toUpperCase()
      .replaceAll("([0-9])([A-Z])", "$1_$2")
      .replaceAll("^([^_A-Z])", "_$1")
      .replaceAll("[^_$A-Z0-9]", "_");
  }

  private static String toClassName(String rValue) {
    Strings.requireNotBlank(rValue);

    String validIdentifier = rValue
      .replaceAll("/+$", "")
      .replaceAll("^([^_a-zA-Z])", "_$1")
      .replaceAll("[^_$a-zA-Z0-9]", "_");

    StringBuilder sb = new StringBuilder()
      .append(Character.toUpperCase(validIdentifier.charAt(0)));

    if (validIdentifier.length() > 1) {
      sb.append(validIdentifier.substring(1));
    }

    return sb.toString();
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

  public String getClassName() {
    return className;
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

  JavaFile generateEnum() {
    ClassName className = ClassName.get(CURSIVE_RTF_PACKAGE, this.className);
    TypeSpec.Builder enumSpecBuilder = terms.foldLeft(TypeSpec.enumBuilder(className), (b, t) ->
      b.addEnumConstant(toConstName(t), TypeSpec.anonymousClassBuilder("$S", t).build())
    )
      .addJavadoc("From RDF::Vocab::$L\n", rubyClassName)
      .addField(FieldSpec.builder(URI.class, "PREFIX", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", prefix)
        .build())
      .addField(FieldSpec.builder(URI.class, "BASE_URI", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("URI.create($S)", uri)
        .build())
      .addMethod(MethodSpec.constructorBuilder()
        .addParameter(String.class, "term")
        .addStatement("this.$N = $N", "term", "term")
        .build())
      .addField(FieldSpec.builder(String.class, "term", Modifier.PRIVATE, Modifier.FINAL).build())
      .addField(FieldSpec.builder(ParameterizedTypeName.get(Lazy.class, URI.class), "uri", Modifier.PRIVATE, Modifier.FINAL)
        .initializer("Lazy.of(this::buildUri)")
        .build())
      .addMethod(MethodSpec.methodBuilder("getTerm")
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addStatement("return this.$N", "term")
        .build())
      .addMethod(MethodSpec.methodBuilder("getUri")
        .addModifiers(Modifier.PUBLIC)
        .returns(URI.class)
        .addStatement("return this.$N.get()", "uri")
        .build())
      .addMethod(MethodSpec.methodBuilder("buildUri")
        .returns(URI.class)
        .addModifiers(Modifier.PRIVATE)
        .addStatement("return URI.create(BASE_URI.toString() + getTerm())")
        .build());

    TypeSpec spec = enumSpecBuilder.build();
    return JavaFile.builder(CURSIVE_RTF_PACKAGE, spec).build();
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
