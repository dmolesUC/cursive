package org.cdlib.cursive.api;

import com.theoryinpractise.halbuilder5.Rel;
import com.theoryinpractise.halbuilder5.Rels;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.cursive.core.async.AsyncStore;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public enum Format {

  HAL("application/hal+json", Format::toHal),
  JSON_LD("application/ld+json", s -> "Hello");

  public static Format DEFAULT = HAL;

//  // TODO: separate out formatter class
//  private static final Lazy<RepresentationFactory> repFactory = Lazy.of(() ->
//    new StandardRepresentationFactory()
//      .withFlag(RepresentationFactory.PRETTY_PRINT)
//      .withFlag(RepresentationFactory.COALESCE_ARRAYS)
//  );

  private final String contentType;
  private final Function1<AsyncStore, String> storeFmt;

  Format(String contentType, Function1<AsyncStore, String> storeFmt) {
    this.contentType = contentType;
    this.storeFmt = storeFmt;
  }

  public String contentType() {
    return contentType;
  }

  public String format(AsyncStore store) {
    return storeFmt.apply(store);
  }

  public static Array<Format> all() {
    return Array.of(values());
  }

  public static Option<Format> byContentType(String contentType) {
    return all().find(ct -> Objects.equals(ct.contentType, contentType));
  }

  // TODO: nio or at least streams
  private static String toHal(AsyncStore s) {
    Map<String, Object> properties = Collections.emptyMap();
    Rel workspacesRel = Rels.singleton("cursive:workspaces");
    ResourceRepresentation<Map<String, Object>> rep = ResourceRepresentation.create("/", properties)
      .withNamespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#{rel}")
      .withRel(workspacesRel)
      .withLink(workspacesRel.rel(), "workspaces");

    JsonRepresentationWriter writer = JsonRepresentationWriter.create();
    StringWriter out = new StringWriter();
    writer.write(rep, out);
    return out.toString();
  }

}
