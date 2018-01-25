package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.cdlib.cursive.util.TestUtils.getResourceAsString;

class HalSerializerTest {

  @Test
  void writesCuries() {
    Namespace acme = new Namespace("acme", "http://docs.acme.com/relations/");
    LinkRelation acmeWidgets = new LinkRelation(acme, "widgets");
    LinkedResult res = new LinkedResult("/orders").withLink(acmeWidgets, "/widgets");

    String expected = getResourceAsString("hal-draft-8.2.json");
    String actual = new HalSerializer().toString(res);

    System.out.println("expected: \n" + expected);
    System.out.println("actual: \n" + actual);

    assertJsonEquals(expected, actual);
  }

}
