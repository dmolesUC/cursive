package org.cdlib.cursive.store;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import java.security.SecureRandom;
import java.util.UUID;

public class Identifiers {

  /**
   * Separate SecureRandom instance per thread to avoid contention
   */
  private static ThreadLocal<NoArgGenerator> generator = ThreadLocal.withInitial(
    () -> Generators.randomBasedGenerator(new SecureRandom())
  );

  public static UUID mintIdentifier() {
    return generator.get().generate();
  }
}
