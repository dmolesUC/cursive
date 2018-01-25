package org.cdlib.cursive.api.s11n;

public class Cursive extends Namespace {

  public static final Namespace CURSIVE = new Cursive();
  public static final LinkRelation WORKSPACES = new LinkRelation(CURSIVE, "workspaces");
  public static final LinkRelation COLLECTIONS = new LinkRelation(CURSIVE, "collections");
  public static final LinkRelation OBJECTS = new LinkRelation(CURSIVE, "objects");
  public static final LinkRelation FILES = new LinkRelation(CURSIVE, "files");

  private Cursive() {
    super("cursive", "https://github.com/dmolesUC3/cursive/blob/master/RELATIONS.md#");
  }
}
