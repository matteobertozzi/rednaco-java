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

package io.github.matteobertozzi.rednaco.collections.maps;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import io.github.matteobertozzi.rednaco.collections.Hashing;
import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.sets.IndexedBuckets;

public class IndexedHashMap<K, V> extends AbstractMap<K, V> {
  private final IndexedBuckets buckets;
  private Object[] entries; // key|val|key|val|...

  public IndexedHashMap(final int initialCapacity) {
    final int capacity = Hashing.tableSizeFor(Math.max(16, initialCapacity));
    this.buckets = new IndexedBuckets(capacity, this::getKeyAtIndex, this::resizeEntries);
    this.entries = new Object[capacity * 2];
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("IndexedHashMap {");
    boolean hasPrevEntry = false;
    for (int i = 0; i < entries.length; i += 2) {
      if (entries[i] != null) {
        if (hasPrevEntry) builder.append(", ");
        builder.append(entries[i]).append(":").append(entries[i + 1]);
        hasPrevEntry = true;
      }
    }
    builder.append("}");
    return builder.toString();
  }

  public final K getKeyAtIndex(final int index) {
    return ArrayUtil.getItemAt(entries, (index << 1));
  }

  public final V getValueAtIndex(final int index) {
    return ArrayUtil.getItemAt(entries, (index << 1) + 1);
  }

  public int getIndex(final Object key) {
    return buckets.getIndex(key);
  }

  @Override
  public void clear() {
    buckets.clear();
  }

  @Override
  public boolean containsKey(final Object key) {
    return buckets.getIndex(key) >= 0;
  }

  @Override
  public V get(final Object key) {
    final int index = buckets.getIndex(key);
    return index >= 0 ? ArrayUtil.getItemAt(entries, (index << 1) + 1) : null;
  }

  @Override
  public V put(final K key, final V value) {
    final int index = buckets.addKey(key);
    final int entryIndex = (index << 1);
    entries[entryIndex] = key;
    final V oldValue = ArrayUtil.getItemAt(entries, entryIndex + 1);
    entries[entryIndex + 1] = value;
    return oldValue;
  }

  public int add(final K key, final V value) {
    final int index = buckets.addKey(key);
    final int entryIndex = (index << 1);
    entries[entryIndex] = key;
    entries[entryIndex + 1] = value;
    return index;
  }

  public int addIfAbsent(final K key, final V value) {
    final int index = buckets.addKey(key);
    final int entryIndex = (index << 1);
    if (entries[entryIndex] == null) {
      entries[entryIndex] = key;
      entries[entryIndex + 1] = value;
    }
    return index;
  }

  public int addIfAbsent(final K key, final Function<? super K,? extends V> mappingFunction) {
    final int index = buckets.addKey(key);
    final int entryIndex = (index << 1);
    if (entries[entryIndex] == null) {
      V newValue;
      if ((newValue = mappingFunction.apply(key)) != null) {
        entries[entryIndex] = key;
        entries[entryIndex + 1] = newValue;
      }
    }
    return index;
  }

  public int removeKey(final K key) {
    final int index = buckets.removeKey(key);
    if (index >= 0) {
      final int entryIndex = (index << 1);
      entries[entryIndex] = null;
      entries[entryIndex + 1] = null;
    }
    return index;
  }

  private void resizeEntries(final int newSize) {
    this.entries = Arrays.copyOf(entries, newSize * 2);
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new EntrySet();
  }

  private class EntrySet extends AbstractSet<Entry<K, V>> {
    @Override
    public int size() {
      return buckets.size();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator();
    }

    @Override
    public boolean add(final Entry<K, V> e) {
      IndexedHashMap.this.add(e.getKey(), e.getValue());
      return true;
    }

    @Override
    public boolean addAll(final Collection<? extends Entry<K, V>> c) {
      for (final Entry<K, V> entry: c) {
        add(entry);
      }
      return true;
    }
  }

  private final class EntryIterator implements Iterator<Entry<K, V>> {
    private int index = 0;
    private int count = 0;

    @Override
    public boolean hasNext() {
      return count < buckets.size();
    }

    @Override
    public Entry<K, V> next() {
      while (index < buckets.size() && entries[index << 1] == null) {
        index++;
      }
      if (index == buckets.size() || entries[index << 1] == null) {
        throw new NoSuchElementException();
      }
      final int entryIndex = index << 1;
      index++;
      count++;
      return Map.entry(ArrayUtil.getItemAt(entries, entryIndex), ArrayUtil.getItemAt(entries, entryIndex + 1));
    }

    @Override
    public void remove() {
      if (index <= 0) {
        throw new IllegalStateException();
      }

      final int entryIndex = (index - 1) << 1;
      if (removeKey(ArrayUtil.getItemAt(entries, entryIndex)) < 0) {
        throw new IllegalStateException();
      }
      count--;
    }
  }
}
