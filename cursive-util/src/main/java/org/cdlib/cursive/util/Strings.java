package org.cdlib.cursive.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Strings {

  public static void requireNotBlank(CharSequence s) {
    Objects.requireNonNull(s);
    if (StringUtils.isBlank(s)) {
      throw new IllegalArgumentException("Argument \"" + s + "\" should not be blank");
    }
  }

  private Strings() {
    // private to prevent accidental instatiation
  }
}
