package org.cdlib.cursive.api.s11n;

public class Pcdm extends Namespace {
  public static Namespace PCDM = new Pcdm();

  public static LinkRelation HAS_FILE = new LinkRelation(PCDM, "hasFile");
  public static LinkRelation FILE_OF = new LinkRelation(PCDM, "fileOf");
  public static LinkRelation HAS_MEMBER = new LinkRelation(PCDM, "hasMember");
  public static LinkRelation MEMBER_OF = new LinkRelation(PCDM, "memberOf");
  public static LinkRelation HAS_RELATED_OBJECT = new LinkRelation(PCDM, "hasRelatedObject");
  public static LinkRelation RELATED_OBJECT_OF = new LinkRelation(PCDM, "relatedObjectOf");

  private Pcdm() {
    super("pcdm", "http://pcdm.org/models#");
  }
}
