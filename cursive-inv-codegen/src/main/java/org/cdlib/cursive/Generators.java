package org.cdlib.cursive;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.embed.ScriptingContainer;

import java.net.URI;

@SuppressWarnings("unchecked")
class Generators {

  private final ScriptingContainer scriptingContainer = initScriptingContainer();

  void generate() {
    Array<Vocab> vocabs = vocabsMap().toArray().map(this::toVocab);
    vocabs.forEach(System.out::println);
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
    Array<String> terms = Array.ofAll((RubyArray) scriptingContainer.runScriptlet(fqConstName + ".send(:props).keys")).map(Object::toString);

    return new Vocab(prefix, iri, terms);
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
    new Generators().generate();
  }
}
