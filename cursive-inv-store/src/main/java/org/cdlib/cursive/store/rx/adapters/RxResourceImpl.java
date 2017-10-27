package org.cdlib.cursive.store.rx.adapters;

import org.cdlib.cursive.core.Resource;
import org.cdlib.cursive.core.rx.RxResource;

import java.util.Objects;

class RxResourceImpl<R extends Resource> implements RxResource {
  protected final R delegate;

  RxResourceImpl(R delegate) {
    Objects.requireNonNull(delegate, () -> String.format("%s must have a delegate", getClass().getSimpleName()));
    this.delegate = delegate;
  }

  @Override
  public String identifier() {
    return delegate.identifier();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RxResourceImpl<?> that = (RxResourceImpl<?>) o;
    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }
}
