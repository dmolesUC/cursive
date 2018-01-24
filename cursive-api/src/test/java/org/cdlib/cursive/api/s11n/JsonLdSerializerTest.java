package org.cdlib.cursive.api.s11n;

import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.cdlib.cursive.util.TestUtils.getResourceAsString;

public class JsonLdSerializerTest {
  @Test
  void writesContext() {
    Namespace schemaDotOrg = new Namespace("schema.org", "http://schema.org/");
    LinkRelation spouse = new LinkRelation(schemaDotOrg, "spouse");
    LinkedResult rs = new LinkedResult("http://dbpedia.org/resource/John_Lennon")
      .withLink(spouse, "http://dbpedia.org/resource/Cynthia_Lennon");

    String expected = getResourceAsString("json-ld-simple-example-curieized.json");
    String actual = new JsonLdSerializer().toString(rs);

    System.out.println("expected: \n" + expected);
    System.out.println("actual: \n" + actual);

    assertJsonEquals(expected, actual);
  }
}
