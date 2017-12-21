package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jruby.RubyHash;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.cdlib.cursive.BuilderUtils.addField;

@SuppressWarnings("unchecked")
class GenVocab {

  // ------------------------------------------------------------
  // Cosntants

  private final Logger log;

  static final String CURSIVE_PACKAGE = "org.cdlib.cursive";
  static final String CURSIVE_RTF_PACKAGE = CURSIVE_PACKAGE + ".rtf";

  // ------------------------------------------------------------
  // Fields

  private final ScriptingContainer scriptingContainer = initScriptingContainer();
  private final Array<Vocab> vocabs = vocabsMap().toArray().map(this::toVocab)
    .removeAll(v -> v.getTerms().isEmpty())
    .sorted();
  public static final ClassName NS_CLASS_NAME = ClassName.get(CURSIVE_PACKAGE, "Namespace");
  public static final ClassName TERM_CLASS_NAME = ClassName.get(CURSIVE_PACKAGE, "Term");
  public static final ClassName VOCAB_CLASS_NAME = ClassName.get(CURSIVE_RTF_PACKAGE, "Vocabulary");

  public GenVocab(Logger log) {
    this.log = log;
  }

  private GenVocab() {
    this(LoggerFactory.getLogger(GenVocab.class));
  }

  // ------------------------------------------------------------
  // Package-private methods

  void generate(File targetDir) {
    Objects.requireNonNull(targetDir, "targetDir cannot be null");
    Path targetPath = targetDir.toPath();

    log("Writing generated files to {}", targetDir.getAbsolutePath());

    // TODO: figure out which functionality belongs in org.cdlib.cursive.rtf.Vocabulary & which in the individual enums

    JavaFile nsIFFile = makeNamespaceInterface();
    JavaFile termIFFile = makeTermInterface();
    JavaFile vocabEnumFile = makeVocabularyEnum();
    Array<JavaFile> rtfVocabularyEnums = makeRtfVocabularyEnums();

    Array<JavaFile> allFiles = Array.of(nsIFFile, termIFFile, vocabEnumFile).appendAll(rtfVocabularyEnums);
    allFiles.forEach(jf -> writeSourceFile(targetPath, jf));

    log("Generated {} files", allFiles.size());
  }

  private void writeSourceFile(Path targetPath, JavaFile jf) {
    Path outPath = Array.of(jf.packageName.split("\\.")).foldLeft(targetPath, Path::resolve);
    outPath.toFile().mkdirs();
    Path filePath = outPath.resolve(jf.typeSpec.name + ".java");
    log.debug("Writing " + filePath);
    try (BufferedWriter out = Files.newBufferedWriter(filePath)) {
      jf.writeTo(out);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Array<JavaFile> makeRtfVocabularyEnums() {
    return vocabs.map(Vocab::generateEnum);
  }

  private JavaFile makeVocabularyEnum() {
    TypeSpec.Builder vocabEnumBuilder = vocabs.foldLeft(
      TypeSpec.enumBuilder(VOCAB_CLASS_NAME),
      (b, v) -> v.addVocabEnumInstance(b)
    )
      .addSuperinterface(NS_CLASS_NAME)
      .addModifiers(Modifier.PUBLIC);
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
    addField(vocabEnumBuilder, constructorBuilder, String.class, "prefix", true);

    FieldSpec field = FieldSpec.builder(URI.class, "baseUri", Modifier.PRIVATE, Modifier.FINAL).build();

    MethodSpec.Builder accessorSpec = MethodSpec.methodBuilder("get" + WordUtils.capitalize(field.name))
      .addModifiers(Modifier.PUBLIC)
      .returns(field.type)
      .addStatement("return this.$N", field.name);
    accessorSpec = accessorSpec.addAnnotation(Override.class);
    vocabEnumBuilder.addField(field).addMethod(accessorSpec.build());

    constructorBuilder
      .addParameter(String.class, field.name)
      .addStatement("this.$N = URI.create($N)", field.name, field.name);


    vocabEnumBuilder.addMethod(constructorBuilder.build());
    TypeSpec vocabEnumSpec = vocabEnumBuilder.build();
    return JavaFile.builder(CURSIVE_RTF_PACKAGE, vocabEnumSpec).build();
  }

  private JavaFile makeTermInterface() {
    TypeSpec.Builder termIFBuilder = TypeSpec.interfaceBuilder(TERM_CLASS_NAME)
      .addModifiers(Modifier.PUBLIC)
      .addMethod(MethodSpec.methodBuilder("getNamespace").returns(NS_CLASS_NAME).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getUri").returns(URI.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getTerm").returns(String.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
    TypeSpec termIFSpec = termIFBuilder.build();
    return JavaFile.builder(CURSIVE_PACKAGE, termIFSpec).build();
  }

  private JavaFile makeNamespaceInterface() {
    TypeSpec.Builder nsIFBuilder = TypeSpec.interfaceBuilder(NS_CLASS_NAME)
      .addModifiers(Modifier.PUBLIC)
      .addMethod(MethodSpec.methodBuilder("getBaseUri").returns(URI.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getPrefix").returns(String.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
    TypeSpec nsIFSpec = nsIFBuilder.build();
    return JavaFile.builder(CURSIVE_PACKAGE, nsIFSpec).build();
  }

  private void log(String msg, Object argument) {
    if (log instanceof org.gradle.api.logging.Logger) {
      ((org.gradle.api.logging.Logger) log).lifecycle(msg, argument);
    } else {
      log.debug(msg, argument);
    }
  }

  // ------------------------------------------------------------
  // Ruby data extraction

  private Vocab toVocab(Tuple2<String, LinkedHashMap<String, Object>> entry) {
    return toVocab(entry._1, entry._2);
  }

  private Vocab toVocab(String id, LinkedHashMap<String, Object> params) {
    String rubyClassName = params.get("class_name").map(Object::toString).getOrElse(id.toUpperCase());
    String fqConstName = "RDF::Vocab::" + rubyClassName;
    String prefix = scriptingContainer.runScriptlet(fqConstName + ".__prefix__").toString();
    URI iri = URI.create(scriptingContainer.runScriptlet(fqConstName + ".to_iri").toString());
    Array<String> terms = Array.ofAll((Iterable<Object>) scriptingContainer.runScriptlet(fqConstName + ".send(:props).keys")).map(Object::toString)
      .removeAll(StringUtils::isBlank);

    Vocab vocab = new Vocab(rubyClassName, prefix, iri, terms);

//    System.out.println(fqConstName + "\t->\t" + vocab.getClassName());

    return vocab;
  }

  private LinkedHashMap<String, LinkedHashMap<String, Object>> vocabsMap() {
    RubyHash vocabsHash = (RubyHash) scriptingContainer.runScriptlet("RDF::Vocab::VOCABS");
    return (LinkedHashMap<String, LinkedHashMap<String, Object>>) toJava(vocabsHash);
  }

  // ------------------------------------------------------------
  // Class methods

  private static LinkedHashMap<String, ?> toJava(RubyHash h) {
    return LinkedHashMap.ofAll(h).map((k, v) -> {
      Object value = v instanceof RubyHash ? toJava((RubyHash) v) : v;
      return Tuple.of(k.toString(), value);
    });
  }

  private static ScriptingContainer initScriptingContainer() {
    ScriptingContainer scriptingContainer = new ScriptingContainer();
    scriptingContainer.runScriptlet("require 'rdf/vocab'");
    return scriptingContainer;
  }

  public static void main(String[] args) throws IOException {
    Path tempDir = Files.createTempDirectory("genvocab");
    new GenVocab().generate(tempDir.toFile());
  }
}
