package org.cdlib.cursive.store.async.adapters;

import io.reactivex.Maybe;
import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.Store;
import org.cdlib.cursive.core.async.AsyncResource;
import org.cdlib.cursive.store.Identifiers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.vavr.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cdlib.cursive.util.RxAssertions.errorEmittedBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AsyncStoreAdapterTest {
  @Test
  void findErrorsOnUnsupportedTypes() {
    UUID identifier = Identifiers.mintIdentifier();

    Resource unsupportedResource = mock(Resource.class);
    when(unsupportedResource.type()).thenReturn(null);

    Store delegate = mock(Store.class);
    when(delegate.find(identifier)).thenReturn(some(unsupportedResource));

    AsyncStoreAdapter<Store> adapter = new AsyncStoreAdapter<>(delegate);
    Maybe<AsyncResource> maybe = adapter.find(identifier);

    Throwable error = errorEmittedBy(maybe);
    assertThat(error).isInstanceOf(IllegalArgumentException.class);
    assertThat(error.getMessage()).startsWith("Unknown resource type");
  }
}
