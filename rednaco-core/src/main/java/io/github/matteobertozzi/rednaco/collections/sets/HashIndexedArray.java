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
import java.util.Objects;

import io.github.matteobertozzi.rednaco.collections.Hashing;
import io.github.matteobertozzi.rednaco.collections.iterators.ArrayIterator;

public class HashIndexedArray<K> extends AbstractSet<K> {
  private final int[] buckets;
  private final int[] table; // hash|next|hash|next|...
  private final K[] keys;

  public HashIndexedArray(final K[] keys) {
    this.keys = keys;

    this.buckets = new int[Hashing.tableSizeFor(keys.length + 7)];
    Arrays.fill(this.buckets, -1);

    this.table = new int[keys.length << 1];
    final int mask = buckets.length - 1;
    for (int i = 0, n = keys.length; i < n; ++i) {
      final int hashCode = Hashing.hash32(keys[i]);
      final int targetBucket = hashCode & mask;
      final int tableIndex = (i << 1);
      this.table[tableIndex] = hashCode;
      this.table[tableIndex + 1] = buckets[targetBucket];
      this.buckets[targetBucket] = i;
    }
  }

  @Override
  public int size() {
    return keys.length;
  }

  public K[] keySet() {
    return keys;
  }

  public K get(final int index) {
    return keys[index];
  }

  @Override
  public boolean contains(final Object key) {
    return getIndex(key) >= 0;
  }

  @Override
  public Iterator<K> iterator() {
    return new ArrayIterator<>(keys);
  }

  public int getIndex(final Object key) {
    final int hashCode = Hashing.hash32(key);
    int index = buckets[hashCode & (buckets.length - 1)];
    while (index >= 0) {
      final int tableIndex = (index << 1);
      if (hashCode == table[tableIndex] && Objects.equals(key, keys[index])) {
        return index;
      }
      index = table[tableIndex + 1];
    }
    return -1;
  }

  @Override
  public String toString() {
    return Arrays.toString(keys);
  }
}
