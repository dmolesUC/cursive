package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.cdlib.cursive.util.TestUtils.getResourceAsString;

public class HalSerializerTest {

  @Test
  void writesCuries() {
    Namespace acme = new Namespace("acme", "http://docs.acme.com/relations/");
    LinkRelation acmeWidgets = new LinkRelation(acme, "widgets");
    ResourceSerialization rs = new ResourceSerialization("/orders").withLink(acmeWidgets, "/widgets");

    String expected = getResourceAsString("hal-draft-8.2.json");
    String actual = new HalSerializer().toString(rs);

    System.out.println("expected: \n" + expected);
    System.out.println("actual: \n" + actual);

    assertJsonEquals(expected, actual);
  }

}
