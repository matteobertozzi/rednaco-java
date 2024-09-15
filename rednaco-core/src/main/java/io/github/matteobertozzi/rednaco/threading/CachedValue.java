package io.github.matteobertozzi.rednaco.threading;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.util.function.FailableSupplier;

public abstract class CachedValue<T> {
  protected final FailableSupplier<T> supplier;
  protected final long cacheNs;

  protected CachedValue(final Duration cachePeriod, final FailableSupplier<T> supplier) {
    this.supplier = supplier;
    this.cacheNs = cachePeriod.toNanos();
  }

  public abstract T get();
  public abstract CachedValue<T> invalidate();

  public static <T> CachedValue<T> newConcurrent(final Duration cachePeriod, final FailableSupplier<T> supplier) {
    return new AtomicCachedValue<>(cachePeriod, supplier);
  }

  private static final class AtomicCachedValue<T> extends CachedValue<T> {
    private final AtomicReference<T> cachedValue = new AtomicReference<>(null);
    private final AtomicLong nextRefreshTs = new AtomicLong(0);

    public AtomicCachedValue(final Duration cachePeriod, final FailableSupplier<T> supplier) {
      super(cachePeriod, supplier);
    }

    public T get() {
      final T value = cachedValue.get();
      if (value == null) {
        return refreshValue();
      }

      if (System.nanoTime() > nextRefreshTs.get()) {
        cachedValue.set(null);
      }
      return value;
    }

    public CachedValue<T> invalidate() {
      nextRefreshTs.set(0);
      cachedValue.set(null);
      return this;
    }

    private T refreshValue() {
      try {
        final T value = supplier.get();
        cachedValue.set(value);
        nextRefreshTs.set(System.nanoTime() + cacheNs);
        return value;
      } catch (final Exception e) {
        Logger.error(e, "unable to refresh cached value");
        throw new RuntimeException(e);
      }
    }
  }
}
