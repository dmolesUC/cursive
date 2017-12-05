package org.cdlib.cursive.store.memory;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import java.security.SecureRandom;

class Identifiers {

  /**
   * Separate SecureRandom instance per thread to avoid contention
   */
  private static ThreadLocal<NoArgGenerator> generator = ThreadLocal.withInitial(
    () -> Generators.randomBasedGenerator(new SecureRandom())
  );

  static String mintIdentifier() {
    return generator.get().generate().toString();
  }
}
