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

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.sets.ArraySet;
import io.github.matteobertozzi.rednaco.collections.sets.HashIndexedArray;

public class HashIndexedArrayMap<K, V> extends AbstractMap<K, V> {
  private final HashIndexedArray<K> keyIndex;
  private final V[] values;

  public HashIndexedArrayMap(final K[] keys, final V[] values) {
    this(new HashIndexedArray<>(keys), values);
  }

  public HashIndexedArrayMap(final HashIndexedArray<K> keyIndex, final V[] values) {
    if (keyIndex.size() != values.length) {
      throw new IllegalArgumentException();
    }
    this.keyIndex = keyIndex;
    this.values = values;
  }

  @Override
  public boolean isEmpty() {
    return values.length == 0;
  }

  public boolean isNotEmpty() {
    return values.length != 0;
  }

  @Override
  public int size() {
    return values.length;
  }

  public HashIndexedArray<K> getKeyIndex() {
    return keyIndex;
  }

  public K[] keys() {
    return keyIndex.keySet();
  }

  public V[] valuesArray() {
    return values;
  }

  @Override
  public boolean containsKey(final Object key) {
    return keyIndex.contains(key);
  }

  public K getKey(final int index) {
    return keyIndex.get(index);
  }

  public int getIndex(final K key) {
    return keyIndex.getIndex(key);
  }

  @Override
  public V get(final Object key) {
    final int index = keyIndex.getIndex(key);
    return index < 0 ? null : getAtIndex(index);
  }

  public V getAtIndex(final int keyIndex) {
    return values[keyIndex];
  }

  public V get(final K key, final V defaultValue) {
    final int index = keyIndex.getIndex(key);
    return index < 0 ? defaultValue : getAtIndex(index, defaultValue);
  }

  public V getAtIndex(final int keyIndex, final V defaultValue) {
    final V value = values[keyIndex];
    return value != null ? value : defaultValue;
  }

  @Override
  public void clear() {
    Arrays.fill(values, null);
  }

  public void put(final int keyIndex, final V value) {
    values[keyIndex] = value;
  }

  public void copyToMap(final Map<K, V> map) {
    for (int i = 0; i < values.length; ++i) {
      map.put(keyIndex.get(i), values[i]);
    }
  }

  public static <K, V> HashIndexedArrayMap<K, V> fromEntity(final V[] entities, final Function<V, K> keySupplier) {
    final K[] keys = ArrayUtil.newArrayFrom(entities, keySupplier);
    return new HashIndexedArrayMap<>(keys, entities);
  }

  @Override
  public String toString() {
    if (values == null || values.length == 0) {
      return "{}";
    }

    final StringBuilder dict = new StringBuilder();
    dict.append("{");
    for (int i = 0; i < values.length; ++i) {
      if (i > 0) dict.append(", ");
      dict.append(keyIndex.get(i)).append(":").append(values[i]);
    }
    dict.append("}");
    return dict.toString();
  }

  @Override
  public Set<K> keySet() {
    return new ArraySet<>(keys());
  }

  @Override
  public Collection<V> values() {
    return new ArraySet<>(values);
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new EntrySet<>(this);
  }

  private static final class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
    private final HashIndexedArrayMap<K, V> map;

    private EntrySet(final HashIndexedArrayMap<K, V> map) {
      this.map = map;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator<>(map);
    }

    @Override
    public int size() {
      return map.size();
    }
  }

  private static final class EntryIterator<K, V> implements Iterator<Map.Entry<K, V>> {
    private final HashIndexedArrayMap<K, V> map;
    private int index;

    private EntryIterator(final HashIndexedArrayMap<K, V> map) {
      this.map = map;
      this.index = 0;
    }

    @Override
    public boolean hasNext() {
      return index < map.size();
    }

    @Override
    public Entry<K, V> next() {
      if (index >= map.size()) {
        throw new NoSuchElementException();
      }

      final K k = map.getKey(index);
      final V v = map.getAtIndex(index);
      index++;
      return Map.entry(k, v);
    }
  }
}
