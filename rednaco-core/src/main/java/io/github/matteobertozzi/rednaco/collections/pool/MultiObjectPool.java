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
package io.github.matteobertozzi.rednaco.collections.pool;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Predicate;

import io.github.matteobertozzi.rednaco.util.function.NoOpFunction;

public class MultiObjectPool<TType, TItem> {
  private final ReentrantLock lock = new ReentrantLock();
  private final LongConsumer sizeNotify;
  private final int[] typeHashes;
  private final Object[] items; // type|item|...
  private int freeSlotIndex;
  private int size;

  public MultiObjectPool(final int size) {
    this(size, NoOpFunction::consumer);
  }

  public MultiObjectPool(final int size, final LongConsumer sizeNotify) {
    this.sizeNotify = sizeNotify;
    this.typeHashes = new int[size];
    this.items = new Object[size << 1];
    this.freeSlotIndex = 0;
    for (int i = 0, n = typeHashes.length - 1; i < n; ++i) {
      typeHashes[i] = (i + 1);
    }
    typeHashes[typeHashes.length - 1] = -1;
  }

  public int size() {
    lock.lock();
    try {
      return size;
    } finally {
      lock.unlock();
    }
  }

  public boolean add(final TType type, final TItem item) {
    final int typeHash = type.hashCode() & 0x7fffffff;
    lock.lock();
    try {
      if (freeSlotIndex < 0) {
        return false;
      }

      final int index = freeSlotIndex;
      final int itemIndex = index << 1;
      if (items[itemIndex] != null) {
        throw new IllegalStateException("try to override an existing slot " + itemIndex);
      }
      freeSlotIndex = typeHashes[index];
      typeHashes[index] = typeHash;
      items[itemIndex] = type;
      items[itemIndex + 1] = item;
      sizeNotify.accept(++size);
      return true;
    } finally {
      lock.unlock();
    }
  }

  public TItem poll(final TType type) {
    final int typeHash = type.hashCode() & 0x7fffffff;
    lock.lock();
    try {
      if (size == 0) {
        return null;
      }

      for (int i = 0; i < typeHashes.length; ++i) {
        if (typeHashes[i] != typeHash) continue;

        final int index = (i << 1);
        final Object indexType = items[index];
        if (!type.equals(indexType)) continue;

        final TItem item = getItem(index);
        removeItem(i, index);
        return item;
      }
      return null;
    } finally {
      lock.unlock();
    }
  }

  public int clean(final Predicate<TItem> predicate, final Consumer<TItem> closeItem) {
    lock.lock();
    try {
      for (int i = 0; i < typeHashes.length; ++i) {
        final int itemIndex = (i << 1);
        final TItem item = getItem(itemIndex);
        if (item == null) continue;

        if (predicate.test(item)) {
          removeItem(i, itemIndex);
          closeItem.accept(item);
        }
      }
      return size;
    } finally {
      lock.unlock();
    }
  }

  private void removeItem(final int slot, final int itemIndex) {
    items[itemIndex] = null;
    items[itemIndex + 1] = null;
    typeHashes[slot] = (freeSlotIndex < 0) ? -1 : freeSlotIndex;
    freeSlotIndex = slot;
    sizeNotify.accept(--size);
  }

  @SuppressWarnings("unchecked")
  private TItem getItem(final int index) {
    return (TItem) items[index + 1];
  }
}
