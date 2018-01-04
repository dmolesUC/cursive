package org.cdlib.cursive.api;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.cursive.api.s11n.HalSerializer;
import org.cdlib.cursive.api.s11n.JsonLdSerializer;
import org.cdlib.cursive.api.s11n.ResourceSerialization;
import org.cdlib.cursive.api.s11n.Serializer;
import org.cdlib.cursive.core.async.AsyncStore;

import java.util.Objects;

import static org.cdlib.cursive.api.s11n.Cursive.WORKSPACES;

public enum Format {

  HAL("application/hal+json", new HalSerializer()),
  JSON_LD("application/ld+json", new JsonLdSerializer());

  public static Format DEFAULT = HAL;

  private final String contentType;
  private Serializer serializer;

  Format(String contentType, Serializer serializer) {
    this.contentType = contentType;
    this.serializer = serializer;
  }

  public String contentType() {
    return contentType;
  }

  public String format(AsyncStore store) {
    return serializer.toString(serialize(store));
  }

  public static Array<Format> all() {
    return Array.of(values());
  }

  public static Option<Format> byContentType(String contentType) {
    return all().find(ct -> Objects.equals(ct.contentType, contentType));
  }

  private static ResourceSerialization serialize(AsyncStore s) {
    return new ResourceSerialization("/")
      .withLink(WORKSPACES, "workspaces");
  }

//  private static String toHal(AsyncStore s) {
//    Map<String, Object> properties = Collections.emptyMap();
//    Rel workspacesRel = Rels.singleton("cursive:workspaces");
//    ResourceRepresentation<Map<String, Object>> rep = ResourceRepresentation.create("/", properties)
//      .withNamespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#{rel}")
//      .withRel(workspacesRel)
//      .withLink(workspacesRel.rel(), "workspaces");
//
//    JsonRepresentationWriter writer = JsonRepresentationWriter.create();
//    StringWriter out = new StringWriter();
//    writer.write(rep, out);
//    return out.toString();
//  }
//
//  private static String toJsonLd(AsyncStore s) {
//    Map<String, Object> context = new LinkedHashMap<>();
//    context.put("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
//    context.put("cursive:workspaces", new HashMap<String, String>(){{
//      put("@type", "@id");
//    }});
//
//    Map<String, Object> object = new LinkedHashMap<>();
//    object.put("@id", "/");
//    object.put("cursive:workspaces", "workspaces");
//
//    JsonLdOptions options = new JsonLdOptions();
//    try {
//      Map<String, Object> compact = JsonLdProcessor.compact(object, context, options);
//      return JsonUtils.toPrettyString(compact);
//    } catch (JsonLdError jsonLdError) {
//      throw new IllegalStateException(jsonLdError);
//    } catch (IOException e) {
//      throw new UncheckedIOException(e);
//    }
//  }
}
