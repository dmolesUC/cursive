package org.cdlib.cursive.api.s11n;

class Iana extends Namespace {

  public static final Namespace IANA = new Iana();
  public static LinkRelation SELF = new LinkRelation(IANA, "self");

  private Iana() {
    super("iana", "http://www.iana.org/assignments/link-relations/#");
  }
}
