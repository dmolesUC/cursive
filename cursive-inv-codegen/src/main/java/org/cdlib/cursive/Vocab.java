package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.Lazy;
import io.vavr.collection.Array;
import org.apache.commons.text.WordUtils;
import org.cdlib.cursive.util.Strings;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Objects;

import static org.cdlib.cursive.GenVocab.CURSIVE_RTF_PACKAGE;

class Vocab implements Comparable<Vocab> {
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

    return WordUtils.capitalize(validIdentifier);
  }

  Array<String> getTerms() {
    return terms;
  }

  JavaFile generateEnum() {
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

    TypeSpec.Builder builder =
      terms.foldLeft(TypeSpec.enumBuilder(className), this::addTerm)
        .addJavadoc("From RDF::Vocab::$L\n", rubyClassName);

    addConstant(builder, URI.class, "PREFIX", "URI.create($S)", this.prefix);
    addConstant(builder, URI.class, "BASE_URI", "URI.create($S)", uri);
    addField(builder, constructorBuilder, String.class, "term");
    addLazyField(builder, URI.class, "uri", "return URI.create(BASE_URI.toString() + getTerm())");
    addLazyField(builder, String.class, "prefixedForm", "return String.format(\"%s:%s\", PREFIX, getTerm())");

    builder.addMethod(constructorBuilder.build());

    TypeSpec spec = builder.build();
    return JavaFile.builder(CURSIVE_RTF_PACKAGE, spec).build();
  }

  private TypeSpec.Builder addTerm(TypeSpec.Builder builder, String term) {
    return builder.addEnumConstant(toConstName(term), TypeSpec.anonymousClassBuilder("$S", term).build());
  }

  // ------------------------------------------------------------
  // Builder helpers

  private void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, Type fieldType, String fieldName) {
    FieldSpec field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();

    MethodSpec accessor = MethodSpec.methodBuilder("get" + WordUtils.capitalize(fieldName))
      .addModifiers(Modifier.PUBLIC)
      .returns(fieldType)
      .addStatement("return this.$N", fieldName)
      .build();

    constructorBuilder
      .addParameter(fieldType, fieldName)
      .addStatement("this.$N = $N", fieldName, fieldName);

    builder.addField(field)
      .addMethod(accessor);
  }

  private void addLazyField(TypeSpec.Builder builder, Type valueType, String fieldName, String valueStmt) {
    String valueMethod = fieldName + "Value";

    MethodSpec valueInitializer = MethodSpec.methodBuilder(valueMethod)
      .returns(valueType)
      .addModifiers(Modifier.PRIVATE)
      .addStatement(valueStmt)
      .build();

    FieldSpec field = FieldSpec.builder(ParameterizedTypeName.get(Lazy.class, valueType), fieldName, Modifier.PRIVATE, Modifier.FINAL)
      .initializer(String.format("Lazy.of(this::%s)", valueMethod))
      .build();

    MethodSpec accessor = MethodSpec.methodBuilder("get" + WordUtils.capitalize(fieldName))
      .addModifiers(Modifier.PUBLIC)
      .returns(valueType)
      .addStatement("return this.$N.get()", fieldName)
      .build();

    builder.addField(field)
      .addMethod(accessor)
      .addMethod(valueInitializer);
  }

  private void addConstant(TypeSpec.Builder builder, Type constClass, String constName, String format, Object... args) {
    FieldSpec fieldSpec = FieldSpec.builder(constClass, constName)
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer(format, args)
      .build();
    builder.addField(fieldSpec);
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
