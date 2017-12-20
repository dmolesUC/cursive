package org.cdlib.cursive;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
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

    ClassName nsClassName = ClassName.get(CURSIVE_PACKAGE, "Namespace");
    TypeSpec.Builder nsIFBuilder = TypeSpec.interfaceBuilder(nsClassName)
      .addMethod(MethodSpec.methodBuilder("getBaseUri").returns(URI.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getPrefix").returns(String.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
    TypeSpec nsIFSpec = nsIFBuilder.build();
    JavaFile nsIFFile = JavaFile.builder(CURSIVE_PACKAGE, nsIFSpec).build();

    ClassName termClassName = ClassName.get(CURSIVE_PACKAGE, "Term");
    TypeSpec.Builder termIFBuilder = TypeSpec.interfaceBuilder(termClassName)
      .addMethod(MethodSpec.methodBuilder("getNamespace").returns(nsClassName).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getUri").returns(URI.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
      .addMethod(MethodSpec.methodBuilder("getTerm").returns(String.class).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
    TypeSpec termIFSpec = termIFBuilder.build();
    JavaFile termIFFile = JavaFile.builder(CURSIVE_PACKAGE, termIFSpec).build();

    ClassName vocabClassName = ClassName.get(CURSIVE_RTF_PACKAGE, "Vocabulary");
    TypeSpec.Builder vocabEnumBuilder = vocabs.foldLeft(TypeSpec.enumBuilder(vocabClassName), (b, v) -> v.addVocabEnumInstance(b));
    vocabEnumBuilder.addSuperinterface(nsClassName);
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
    addField(vocabEnumBuilder, constructorBuilder, String.class, "prefix", true);
    addField(vocabEnumBuilder, constructorBuilder, URI.class, "baseUri", true);
    vocabEnumBuilder.addMethod(constructorBuilder.build());
    TypeSpec vocabEnumSpec = vocabEnumBuilder.build();
    JavaFile vocabEnumFile = JavaFile.builder(CURSIVE_RTF_PACKAGE, vocabEnumSpec).build();

    Array<JavaFile> files = Array.of(nsIFFile, termIFFile, vocabEnumFile)
        .appendAll(vocabs.map(Vocab::generateEnum));

    files.forEach(jf -> {
      Path outPath = Array.of(jf.packageName.split("\\.")).foldLeft(targetPath, Path::resolve);
      outPath.toFile().mkdirs();
      Path filePath = outPath.resolve(jf.typeSpec.name + ".java");
      log.debug("Writing " + filePath);
      try (BufferedWriter out = Files.newBufferedWriter(filePath)) {
        jf.writeTo(out);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    log("Generated {} files", files.size());
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
