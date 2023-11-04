package io.github.matteobertozzi.rednaco.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LimitedIterator<T> implements Iterable<T>, Iterator<T> {
  private final Iterator<T> iterator;
  private int avail;

  public LimitedIterator(final int limit, final Iterator<T> iterator) {
    this.iterator = iterator;
    this.avail = limit;
  }

  @Override
  public boolean hasNext() {
    return avail > 0 && iterator.hasNext();
  }

  @Override
  public T next() {
    if (avail == 0) {
      throw new NoSuchElementException();
    }

    avail--;
    return iterator.next();
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }
}
