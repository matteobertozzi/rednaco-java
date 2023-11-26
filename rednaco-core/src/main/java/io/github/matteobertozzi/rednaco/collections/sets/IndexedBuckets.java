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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

import io.github.matteobertozzi.rednaco.collections.Hashing;

public class IndexedBuckets {
  private final IntFunction<Object> keyAtIndex;
  private final IntConsumer resizeEntries;

  private int[] buckets;
  private int[] table; // hash|next|hash|next|...
  private int entriesAvail;
  private int entriesIndex;
  private int freeList;
  private int count;

  public IndexedBuckets(final int initialCapacity,
      final IntFunction<Object> keyAtIndex,
      final IntConsumer resizeEntries) {
    this.keyAtIndex = keyAtIndex;
    this.resizeEntries = resizeEntries;

    final int capacity = Hashing.tableSizeFor(Math.max(16, initialCapacity));
    this.buckets = new int[capacity];
    this.table = new int[capacity << 1];
    this.entriesAvail = capacity;
    this.entriesIndex = 0;
    this.freeList = -1;
    this.count = 0;
    Arrays.fill(buckets, -1);
    Arrays.fill(table, -1);
  }

  public void clear() {
    Arrays.fill(buckets, -1);
    Arrays.fill(table, -1);
    this.entriesAvail = (table.length >> 1);
    this.entriesIndex = 0;
    this.freeList = -1;
    this.count = 0;
  }

  public boolean isEmpty() {
    return count == 0;
  }

  public boolean isNotEmpty() {
    return count != 0;
  }

  public int size() {
    return count;
  }

  public boolean contains(final Object key) {
    return getIndex(key) >= 0;
  }

  public int getIndex(final Object key) {
    final int hashCode = Hashing.hash32(key);
    final int index = buckets[hashCode & (buckets.length - 1)];
    return getIndex(key, hashCode, index);
  }

  private int getIndex(final Object key, final int hashCode, int index) {
    while (index >= 0) {
      final int tableIndex = (index << 1);
      if (hashCode == table[tableIndex] && Objects.equals(keyAtIndex.apply(index), key)) {
        return index;
      }
      index = table[tableIndex + 1];
    }
    return -1;
  }

  public int addKey(final Object key) {
    final int hashCode = Hashing.hash32(key);
    final int targetBucket = hashCode & (buckets.length - 1);
    final int index = getIndex(key, hashCode, buckets[targetBucket]);
    if (index >= 0) return index;

    return insertNewEntry(hashCode, targetBucket);
  }

  private int insertNewEntry(final int hashCode, int targetBucket) {
    final int index;
    if (freeList >= 0) {
      index = freeList;
      freeList = table[(index << 1) + 1];
    } else {
      if (entriesAvail == 0) {
        resize();
        targetBucket = hashCode & (buckets.length - 1);
      }
      entriesAvail--;
      index = entriesIndex++;
    }

    count++;
    final int tableIndex = (index << 1);
    this.table[tableIndex] = hashCode;
    this.table[tableIndex + 1] = buckets[targetBucket];
    this.buckets[targetBucket] = index;
    return index;
  }

  public int removeKey(final Object key) {
    final int hashCode = Hashing.hash32(key);
    final int targetBucket = hashCode & (buckets.length - 1);
    int index = buckets[targetBucket];
    int last = -1;
    while (index >= 0) {
      final int tableIndex = (index << 1);
      final int next = table[tableIndex + 1];
      if (hashCode == table[tableIndex] && Objects.equals(keyAtIndex.apply(index), key)) {
        if (last < 0) {
          buckets[targetBucket] = next;
        } else {
          table[(last << 1) + 1] = next;
        }
        table[tableIndex] = -1;
        table[tableIndex + 1] = freeList;
        freeList = index;
        count--;
        return index;
      }
      last = index;
      index = next;
    }
    return -1;
  }

  private void resize() {
    final int newCapacity = entriesIndex << 1;
    if (newCapacity < 0) {
      throw new IllegalStateException("Map too big. size=" + entriesIndex);
    }
    resize(newCapacity);
  }

  private void resize(final int newSize) {
    this.buckets = new int[newSize];
    Arrays.fill(buckets, -1);
    this.table = Arrays.copyOf(table, newSize << 1);
    Arrays.fill(table, entriesIndex << 1, table.length, -1);
    resizeEntries.accept(newSize);
    this.entriesAvail = newSize - entriesIndex;

    final int mask = (buckets.length - 1);
    for (int i = 0; i < entriesIndex; ++i) {
      final Object key = keyAtIndex.apply(i);
      if (key == null) continue;

      final int tableIndex = i << 1;
      final int hashCode = table[tableIndex];
      final int targetBucket = hashCode & mask;
      table[tableIndex + 1] = buckets[targetBucket];
      buckets[targetBucket] = i;
    }
  }
}
