package org.cdlib.cursive.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class TestUtils {
  public static String getResourceAsString(String resource) {
    URL resourceUrl = ClassLoader.getSystemClassLoader().getResource(resource);
    try {
      return IOUtils.toString(resourceUrl);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
