package org.cdlib.cursive.api.s11n;

import com.theoryinpractise.halbuilder5.Rel;
import com.theoryinpractise.halbuilder5.Rels;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;

import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.cdlib.cursive.api.s11n.Iana.IANA;

/**
 * Basic <a href="http://stateless.co/hal_specification.html">Hypertext Application Language</a> serializer.
 * Some things we currently don't, and for Cursive purposes, may not ever support:
 * <ul>
 * <li>non-namespaced (non-CURIEd) link relations</li>
 * <li>templated links</li>
 * <li>embedded resources</li>
 * </ul>
 * Some things we should support, but don't yet:
 * <ul>
 * <li>
 * TODO: properties (opaque values) -- separte "LinkRelation" and "Term"?
 * </li>
 * </ul>
 * Some things we don't have an immediately obvious use case for, but should maybe support in the future:
 * <ul>
 * <li>
 * TODO: link arrays (collection relations)
 * </li>
 * </ul>
 */
public class HalSerializer implements Serializer {

  private final JsonRepresentationWriter jsonWriter = JsonRepresentationWriter.create();

  @Override
  public String toString(LinkedResult result) {
    URI selfPath = result.selfPath();
    String selfPathStr = selfPath.toString();

    Map<String, Object> properties = Collections.emptyMap();

    ResourceRepresentation<Map<String, Object>> halRep = ResourceRepresentation.create(selfPathStr, properties);
    halRep = result.allNamespaces()
      .filter((ns) -> !IANA.equals(ns))
      .foldLeft(halRep, (rep, ns) ->
        rep.withNamespace(ns.getPrefix(), toCurieHref(ns))
      );
    halRep = result.allRelations()
      .foldLeft(halRep, (rep, lr) ->
        rep.withRel(toRel(lr))
      );
    halRep = result.links()
      .foldLeft(halRep, (rep, link) ->
        rep.withLink(toRel(link.rel()).rel(), link.target().toString())
      );

    StringWriter out = new StringWriter();
    jsonWriter.write(halRep, out);
    return out.toString();
  }

  private String toCurieHref(Namespace ns) {
    return String.format("%s{rel}", ns.getUriBase());
  }

  // TODO: when is Rels.singleton() not appropriate?
  private static Rel toRel(LinkRelation relation) {
    if (IANA.equals(relation.namespace())) {
      return Rels.singleton(relation.term());
    }
    return Rels.singleton(relation.prefixedForm());
  }
}
