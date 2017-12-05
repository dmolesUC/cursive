package org.cdlib.cursive.api;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.theoryinpractise.halbuilder5.Rel;
import com.theoryinpractise.halbuilder5.Rels;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.cursive.core.async.AsyncStore;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.*;

public enum Format {

  HAL("application/hal+json", Format::toHal),
  JSON_LD("application/ld+json", Format::toJsonLd);

  public static Format DEFAULT = HAL;

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

  private static String toJsonLd(AsyncStore s) {
    Map<String, Object> context = new LinkedHashMap<>();
    context.put("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
    context.put("cursive:workspaces", new HashMap<String, String>(){{
      put("@type", "@id");
    }});

    Map<String, Object> object = new LinkedHashMap<>();
    object.put("@id", "/");
    object.put("cursive:workspaces", "workspaces");

    JsonLdOptions options = new JsonLdOptions();
    try {
      Map<String, Object> compact = JsonLdProcessor.compact(object, context, options);
      return JsonUtils.toPrettyString(compact);
    } catch (JsonLdError jsonLdError) {
      throw new IllegalStateException(jsonLdError);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
