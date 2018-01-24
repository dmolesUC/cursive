package org.cdlib.cursive.api.s11n;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonLdSerializer implements Serializer {

  public static final Map<String, String> LINK_REL_PROPERTIES = Collections.unmodifiableMap(Collections.singletonMap("@type", "@id"));

  @Override
  public String toString(LinkedResult result) {
    URI selfPath = result.selfPath();
    String selfPathStr = selfPath.toString();

    Map<String, Object> context = new LinkedHashMap<>();
    context = result.allNamespaces()
      .foldLeft(context, (ctx, ns) -> {
          ctx.put(ns.getPrefix(), ns.getUriBase().toString());
          return ctx;
        }
      );
    context = result.allRelations()
      .foldLeft(context, (ctx, rel) -> {
          ctx.put(rel.prefixedForm(), LINK_REL_PROPERTIES);
          return ctx;
        }
      );

    Map<String, Object> object = new LinkedHashMap<>();
    object.put("@id", selfPathStr);
    object = result.links().foldLeft(object, (obj, link) -> {
      obj.put(link.rel().prefixedForm(), link.target().toString());
      return obj;
    });

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
