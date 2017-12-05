package org.cdlib.cursive;

import com.squareup.javapoet.JavaFile;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jruby.RubyHash;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unchecked")
class GenVocab {

  // TODO: fix SLF4J configuration
  private static final Logger log = LoggerFactory.getLogger(GenVocab.class);

  static final String CURSIVE_RTF_PACKAGE = "org.cdlib.cursive.rtf";

  private final ScriptingContainer scriptingContainer = initScriptingContainer();
  private final Array<Vocab> vocabs = vocabsMap().toArray().map(this::toVocab)
    .removeAll(v -> v.getTerms().isEmpty())
    .sorted();

  void generate(File targetDir) {
    Path srcPath = Array.of(CURSIVE_RTF_PACKAGE.split("\\.")).foldLeft(targetDir.toPath(), Path::resolve);
    log.debug("Writing generated files to %s", srcPath);
    srcPath.toFile().mkdirs();

    Array<JavaFile> files = vocabs.map(Vocab::generateEnum);
    files.forEach(jf -> {
      Path filePath = srcPath.resolve(jf.typeSpec.name + ".java");
      try (BufferedWriter out = Files.newBufferedWriter(filePath)) {
        jf.writeTo(out);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    log.debug("Generated %d files", files.size());
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

  // ------------------------------------------------------------
  // Main program

  public static void main(String[] args) {
    new GenVocab().generate(new File(args[0]));
  }
}