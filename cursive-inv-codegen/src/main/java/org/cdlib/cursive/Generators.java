package org.cdlib.cursive;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import org.apache.commons.lang3.StringUtils;
import org.jruby.RubyHash;
import org.jruby.embed.ScriptingContainer;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

@SuppressWarnings("unchecked")
class Generators {

  public static final String CURSIVE_RTF_PACKAGE = "org.cdlib.cursive.rtf";
  private static final ClassName VOCAB_CLASS_NAME = ClassName.get(CURSIVE_RTF_PACKAGE, "Vocabulary");
  private static final ClassName TERM_CLASS_NAME = ClassName.get(CURSIVE_RTF_PACKAGE, "Term");

  private final ScriptingContainer scriptingContainer = initScriptingContainer();
  private final Array<Vocab> vocabs = vocabsMap().toArray().map(this::toVocab).sorted();
  private final Set<String> ambiguousTerms = findAmbiguousTerms(vocabs);

  void generate() {
    // TODO: add enum methods, fields, bodies

    TypeSpec.Builder vocabsBuilder = vocabs.foldLeft(
      TypeSpec.enumBuilder(VOCAB_CLASS_NAME),
      (b, v) -> v.addVocabEnumConstant(b)
    ).addModifiers(Modifier.PUBLIC);

    vocabsBuilder = addField(vocabsBuilder, String.class, "prefix", "getPrefix");
    vocabsBuilder = addField(vocabsBuilder, URI.class, "uri", "getURI");

    TypeSpec vocabsSpec = vocabsBuilder.build();
    JavaFile.Builder vocabsFileBuilder = JavaFile.builder(CURSIVE_RTF_PACKAGE, vocabsSpec);

    // TODO: what if we just had one enum per vocab, w/static methods for prefix & URI?

    TypeSpec.Builder termsBuilder = vocabs.foldLeft(
      TypeSpec.enumBuilder(TERM_CLASS_NAME),
      (b, v) ->
        v.getTerms().foldLeft(b, (b1, t) ->
          b1.addEnumConstant(getTermConstName(v, t))
        )
    );
    TypeSpec termsSpec = termsBuilder.build();
    JavaFile.Builder termsFileBuilder = JavaFile.builder(CURSIVE_RTF_PACKAGE, termsSpec);

    List<JavaFile.Builder> fileBuilders = List.of(vocabsFileBuilder, termsFileBuilder);
    List<JavaFile> files = fileBuilders.map(JavaFile.Builder::build);
    files.forEach(f -> {
      try {
        f.writeTo(System.out);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }

  private TypeSpec.Builder addField(TypeSpec.Builder vocabsBuilder, Class<?> fieldType, String fieldName, String getterName) {
    vocabsBuilder = vocabsBuilder
      .addMethod(MethodSpec.constructorBuilder()
        .addParameter(fieldType, fieldName)
        .addStatement("this.$N = $N", fieldName, fieldName)
        .build())
      .addField(URI.class, fieldName)
      .addMethod(MethodSpec.methodBuilder(getterName)
        .addModifiers(Modifier.PUBLIC)
        .returns(URI.class)
        .addStatement("return this.$N", fieldName)
        .build());
    return vocabsBuilder;
  }

  // ------------------------------------------------------------
  // Code generation helpers

  private String getTermConstName(Vocab vocabulary, String term) {
    String vocabConst = vocabulary.getConstName();
    String termConstBase = Vocab.toConstName(term);
    return ambiguousTerms.contains(term) ? String.format("%s_%s", termConstBase, vocabConst) : termConstBase;
  }

  // ------------------------------------------------------------
  // Ruby data extraction

  private Vocab toVocab(Tuple2<String, LinkedHashMap<String, Object>> entry) {
    return toVocab(entry._1, entry._2);
  }

  private Vocab toVocab(String id, LinkedHashMap<String, Object> params) {
    String fqConstName = "RDF::Vocab::" + params.get("class_name").map(Object::toString).getOrElse(id.toUpperCase());
    String prefix = scriptingContainer.runScriptlet(fqConstName + ".__prefix__").toString();
    URI iri = URI.create(scriptingContainer.runScriptlet(fqConstName + ".to_iri").toString());
    Array<String> terms = Array.ofAll((Iterable<Object>) scriptingContainer.runScriptlet(fqConstName + ".send(:props).keys")).map(Object::toString)
      .removeAll(StringUtils::isBlank);

    return new Vocab(prefix, iri, terms);
  }

  private LinkedHashMap<String, LinkedHashMap<String, Object>> vocabsMap() {
    RubyHash vocabsHash = (RubyHash) scriptingContainer.runScriptlet("RDF::Vocab::VOCABS");
    return (LinkedHashMap<String, LinkedHashMap<String, Object>>) toJava(vocabsHash);
  }

  // ------------------------------------------------------------
  // Class methods

  private static Set<String> findAmbiguousTerms(Array<Vocab> vocabs) {
    return vocabs.foldLeft(HashMap.<String, Integer>empty(),
      (m, v) ->
        v.getTerms().foldLeft(m,
          (m1, t) ->
            m1.put(t, m1.get(t).map(c -> c + 1).getOrElse(1))
        )
    ).toSet()
      .filter(t -> t._2 > 1)
      .map(Tuple2::_1);
  }

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
    new Generators().generate();
  }
}
