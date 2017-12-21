package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.collection.Array;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.net.URI;
import java.util.Objects;

import static org.cdlib.cursive.BuilderUtils.addConstant;
import static org.cdlib.cursive.BuilderUtils.addField;
import static org.cdlib.cursive.BuilderUtils.addLazyField;
import static org.cdlib.cursive.GenVocab.CURSIVE_PACKAGE;
import static org.cdlib.cursive.GenVocab.CURSIVE_RTF_PACKAGE;

class Vocab implements Comparable<Vocab> {

  public static final boolean IS_OVERRIDE = true;
  public static final boolean NOT_OVERRIDE = false;

  private final String prefix;
  private final URI uri;
  private final Array<String> terms;
  private final String constName;
  private final ClassName className;
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
    this.className = ClassName.get(CURSIVE_RTF_PACKAGE, toClassName(rubyClassName));
  }

  private static String toConstName(String rValue) {
    String constName = rValue
      .replaceAll("/+$", "")
      .replaceAll("([a-z])([A-Z])", "$1_$2")
      .replaceAll("([0-9])([A-Z])", "$1_$2")
      .toUpperCase()
      .replaceAll("([0-9])([A-Z])", "$1_$2")
      .replaceAll("^([^_A-Z])", "_$1")
      .replaceAll("[^_$A-Z0-9]", "_");
    return constName;
  }

  private static String toClassName(String rValue) {
    String validIdentifier = rValue
      .replaceAll("/+$", "")
      .replaceAll("^([^_a-zA-Z])", "_$1")
      .replaceAll("[^_$a-zA-Z0-9]", "_");

    return WordUtils.capitalize(validIdentifier);
  }

  Array<String> getTerms() {
    return terms;
  }

  TypeSpec.Builder addVocabEnumInstance(TypeSpec.Builder vocabEnumBuilder) {
    vocabEnumBuilder.addEnumConstant(constName, TypeSpec.anonymousClassBuilder("$S, $S", prefix, uri).build());
    return vocabEnumBuilder;
  }

  JavaFile generateEnum() {
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

    // TODO: initialize namespace in addTerm()
    TypeSpec.Builder builder =
      terms.foldLeft(TypeSpec.enumBuilder(className), this::addTerm)
        .addJavadoc("From RDF::Vocab::$L\n", rubyClassName)
        .addSuperinterface(ClassName.get(CURSIVE_PACKAGE, "Term"));

    addConstant(builder, String.class, "CURIE_PREFIX", "$S", this.prefix);
    addConstant(builder, URI.class, "BASE_URI", "java.net.URI.create($S)", uri);
    addField(builder, constructorBuilder, ClassName.get(CURSIVE_PACKAGE, "Namespace"), "namespace", IS_OVERRIDE);
    addField(builder, constructorBuilder, String.class, "term", IS_OVERRIDE);
    addLazyField(builder, URI.class, "uri", "return java.net.URI.create(BASE_URI.toString() + getTerm())", IS_OVERRIDE);
    addLazyField(builder, String.class, "prefixedForm", "return String.format(\"%s:%s\", prefix(), getTerm())", NOT_OVERRIDE);

    MethodSpec prefixAccessor = MethodSpec.methodBuilder("prefix")
      .addModifiers(Modifier.PUBLIC)
      .addModifiers(Modifier.STATIC)
      .returns(String.class)
      .addStatement("return CURIE_PREFIX")
      .build();
    builder.addMethod(prefixAccessor);

    MethodSpec baseUriAccessor = MethodSpec.methodBuilder("baseUri")
      .addModifiers(Modifier.PUBLIC)
      .addModifiers(Modifier.STATIC)
      .returns(URI.class)
      .addStatement("return BASE_URI")
      .build();
    builder.addMethod(baseUriAccessor);

    builder.addMethod(constructorBuilder.build());

    TypeSpec spec = builder.build();
    return JavaFile.builder(CURSIVE_RTF_PACKAGE, spec).build();
  }

  private TypeSpec.Builder addTerm(TypeSpec.Builder builder, String term) {
    return builder.addEnumConstant(toConstName(term), TypeSpec.anonymousClassBuilder("Vocabulary.$L, $S", constName, term).build());
  }

  // ------------------------------------------------------------
  // Overrides

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
