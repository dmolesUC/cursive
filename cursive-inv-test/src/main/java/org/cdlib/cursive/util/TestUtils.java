package org.cdlib.cursive.util;

import io.vavr.control.Option;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class TestUtils {
  public static String getResourceAsString(String resource) {
    URL resourceUrl = Option.of(TestUtils.class.getClassLoader().getResource(resource))
      .getOrElseThrow(() -> new IllegalArgumentException("No such resource: " + resource));

    try {
      return IOUtils.toString(resourceUrl);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
