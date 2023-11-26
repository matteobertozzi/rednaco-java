/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.matteobertozzi.rednaco.collections.sets;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.github.matteobertozzi.rednaco.collections.Hashing;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.iterators.PeekIterator;

public class IndexedHashSet<T> extends AbstractSet<T> {
  private final IndexedBuckets buckets;
  private Object[] keys;

  public IndexedHashSet() {
    this(0);
  }

  public IndexedHashSet(final int initialCapacity) {
    final int capacity = Hashing.tableSizeFor(Math.max(16, initialCapacity));
    this.buckets = new IndexedBuckets(capacity, this::getAtIndex, this::resizeEntries);
    this.keys = new Object[capacity];
  }

  public boolean isEmpty() {
    return buckets.isEmpty();
  }

  public int size() {
    return buckets.size();
  }

  public boolean contains(final Object key) {
    return buckets.contains(key);
  }

  public int getIndex(final Object key) {
    return buckets.getIndex(key);
  }

  public final T getAtIndex(final int index) {
    return index < keys.length ? ArrayUtil.getItemAt(keys, index) : null;
  }

  @Override
  public boolean add(final T key) {
    final int index = buckets.addKey(key);
    final boolean hasKey = keys[index] != null;
    keys[index] = key;
    return hasKey;
  }

  public int addKey(final T key) {
    final int index = buckets.addKey(key);
    keys[index] = key;
    return index;
  }

  @Override
  public boolean remove(final Object key) {
    return buckets.removeKey(key) >= 0;
  }

  public int removeKey(final T key) {
    final int index = buckets.removeKey(key);
    if (index >= 0) {
      keys[index] = null;
    }
    return index;
  }

  @Override
  public void clear() {
    buckets.clear();
    Arrays.fill(keys, null);
  }

  private void resizeEntries(final int newSize) {
    this.keys = Arrays.copyOf(keys, newSize);
  }

  @Override
  public Object[] toArray() {
    return toArray(ArrayUtil.EMPTY_OBJECT_ARRAY);
  }

  @Override
  public <E> E[] toArray(final E[] a) {
    final E[] result = ArrayUtil.newArray(buckets.size());
    for (int i = 0, ri = 0; i < keys.length; ++i) {
      if (keys[i] != null) {
        result[ri++] = ArrayUtil.getItemAt(keys, i);
      }
    }
    return result;
  }

  @Override
  public Iterator<T> iterator() {
    return new IndexedHashSetIterator();
  }

  private final class IndexedHashSetIterator implements PeekIterator<T> {
    private int nextIndex = 0;

    private IndexedHashSetIterator() {
      skipNulls();
    }

    @Override
    public boolean hasNext() {
      return nextIndex < keys.length;
    }

    @Override
    public T next() {
      if (nextIndex >= keys.length) {
        throw new NoSuchElementException();
      }

      final T key = ArrayUtil.getItemAt(keys, nextIndex++);
      skipNulls();
      return key;
    }

    @Override
    public T peek() {
      return nextIndex < keys.length ? ArrayUtil.getItemAt(keys, nextIndex) : null;
    }

    @Override
    public void remove() {
      removeKey(ArrayUtil.getItemAt(keys, nextIndex));
    }

    private void skipNulls() {
      while (nextIndex < keys.length && keys[nextIndex] == null) {
        nextIndex++;
      }
    }
  }
}
