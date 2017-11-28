package org.cdlib.cursive.api;

import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;
import io.vavr.Function1;
import io.vavr.Lazy;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import org.cdlib.cursive.core.async.AsyncStore;

import java.util.Objects;

public enum Format {

  HAL("application/hal+json", Format::toHal),
  JSON_LD("application/ld+json", s -> "Hello");

  public static Format DEFAULT = HAL;

  // TODO: separate out formatter class
  private static final Lazy<RepresentationFactory> repFactory = Lazy.of(() ->
    new StandardRepresentationFactory().withFlag(RepresentationFactory.PRETTY_PRINT)
  );

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

  private static String toHal(AsyncStore s) {
    RepresentationFactory rf = repFactory.get();
    return rf.newRepresentation("/")
      .withNamespace("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#{rel}")
      .withLink("cursive:workspaces", "workspaces")
      .toString(RepresentationFactory.HAL_JSON)
    ;
  }

}
