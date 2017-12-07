package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.Lazy;
import io.vavr.collection.Array;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Objects;

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
    return builder.addEnumConstant(toConstName(term), TypeSpec.anonymousClassBuilder("$S", term).build());
  }

  // ------------------------------------------------------------
  // Builder helpers

  private void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, Type fieldType, String fieldName, boolean isOverride) {
    FieldSpec field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();
    addField(builder, constructorBuilder, field, isOverride);
  }

  private void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, TypeName fieldType, String fieldName, boolean isOverride) {
    FieldSpec field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();
    addField(builder, constructorBuilder, field, isOverride);
  }

  private void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, FieldSpec field, boolean isOverride) {
    MethodSpec.Builder accessorSpec = MethodSpec.methodBuilder("get" + WordUtils.capitalize(field.name))
      .addModifiers(Modifier.PUBLIC)
      .returns(field.type)
      .addStatement("return this.$N", field.name);

    if (isOverride) {
      accessorSpec = accessorSpec.addAnnotation(Override.class);
    }

    constructorBuilder
      .addParameter(field.type, field.name)
      .addStatement("this.$N = $N", field.type, field.name);

    builder.addField(field)
      .addMethod(accessorSpec.build());
  }

  private void addLazyField(TypeSpec.Builder builder, Type valueType, String fieldName, String valueStmt, boolean isOverride) {
    String valueMethod = fieldName + "Value";

    MethodSpec valueInitializer = MethodSpec.methodBuilder(valueMethod)
      .returns(valueType)
      .addModifiers(Modifier.PRIVATE)
      .addStatement(valueStmt)
      .build();

    FieldSpec field = FieldSpec.builder(ParameterizedTypeName.get(Lazy.class, valueType), fieldName, Modifier.PRIVATE, Modifier.FINAL)
      .initializer(String.format("Lazy.of(this::%s)", valueMethod))
      .build();

    MethodSpec.Builder accessorSpec = MethodSpec.methodBuilder("get" + WordUtils.capitalize(fieldName))
      .addModifiers(Modifier.PUBLIC)
      .returns(valueType)
      .addStatement("return this.$N.get()", fieldName);

    if (isOverride) {
      accessorSpec = accessorSpec.addAnnotation(Override.class);
    }

    builder.addField(field)
      .addMethod(accessorSpec
        .build())
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
