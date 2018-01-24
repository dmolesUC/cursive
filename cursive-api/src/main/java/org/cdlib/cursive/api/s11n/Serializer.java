package org.cdlib.cursive.api.s11n;

public interface Serializer {
  // TODO: nio or at least streams
  String toString(LinkedResult result);
}
