package org.cdlib.cursive.util;

import io.vavr.control.Option;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TestUtils {

  public static final UUID NIL_UUID = new UUID(0, 0);

  public static String getResourceAsString(String resource) {
    var resourceUrl = Option.of(TestUtils.class.getClassLoader().getResource(resource))
      .getOrElseThrow(() -> new IllegalArgumentException("No such resource: " + resource));

    try {
      return IOUtils.toString(resourceUrl, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static UUID badUUID() {
    return NIL_UUID;
  }
}
