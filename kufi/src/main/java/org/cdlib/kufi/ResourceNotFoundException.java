package org.cdlib.kufi;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
  private final UUID id;
  private final ResourceType<?> type;

  public ResourceNotFoundException(UUID id, ResourceType<?> type) {
    super("Unable to find resource " + type + " with ID " + id);
    this.id = id;
    this.type = type;
  }

  public UUID id() {
    return id;
  }

  public ResourceType<?> type() {
    return type;
  }
}
