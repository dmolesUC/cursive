package org.cdlib.cursive.api.s11n;

public class Iana extends Namespace {

  public static Namespace IANA = new Iana();
  public static LinkRelation SELF = new LinkRelation(IANA, "self");

  private Iana() {
    super("iana", "http://www.iana.org/assignments/link-relations/#");
  }
}
