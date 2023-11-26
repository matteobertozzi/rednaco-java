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

package io.github.matteobertozzi.rednaco.collections.arrays;

import java.util.Arrays;
import java.util.function.Consumer;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil.ArrayConsumer;

public class ObjectArray<T> {
  private T[] items;
  private int count;

  public ObjectArray(final Class<T> classOfT, final int initialCapacity) {
    this.items = ArrayUtil.newArray(initialCapacity, classOfT);
    this.count = 0;
  }

  public void reset() {
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

  public T[] rawBuffer() {
    return items;
  }

  public T[] buffer() {
    return Arrays.copyOf(items, count);
  }

  public T[] drain() {
    final T[] result;
    if (items.length == count) {
      result = rawBuffer();
      this.items = ArrayUtil.emptyArray();
    } else if (count == 0) {
      result = ArrayUtil.emptyArray();
    } else {
      result = buffer();
    }
    this.count = 0;
    return result;
  }

  public T get(final int index) {
    return ArrayUtil.getItemAt(items, index);
  }

  public void set(final int index, final T value) {
    items[index] = value;
  }

  public void add(final T value) {
    if (count == items.length) {
      this.items = Arrays.copyOf(items, count + 16);
    }
    items[count++] = value;
  }

  public void add(final T[] value) {
    add(value, 0, value.length);
  }

  public void add(final T[] value, final int off, final int len) {
    if ((count + len) >= items.length) {
      this.items = Arrays.copyOf(items, count + len + 16);
    }
    System.arraycopy(value, off, items, count, len);
    count += len;
  }

  public void insert(final int index, final T value) {
    if (index == count) {
      if (count == items.length) {
        this.items = Arrays.copyOf(items, count + 16);
      }
      count++;
    }
    items[index] = value;
  }

  public void swap(final int aIndex, final int bIndex) {
    ArrayUtil.swap(items, aIndex, bIndex);
  }

  public void fill(final int value) {
    Arrays.fill(items, value);
  }

  public int indexOf(final int offset, final int value) {
    return ArraySearchUtil.indexOf(items, 0, count, value);
  }

  public void forEach(final ArrayConsumer<T> consumer) {
    consumer.accept(rawBuffer(), 0, count);
  }

  public void forEach(final Consumer<T> consumer) {
    for (int i = 0; i < count; ++i) {
      consumer.accept(ArrayUtil.getItemAt(items, i));
    }
  }

  @Override
  public String toString() {
    return "ObjectArray [count=" + count + ", items=" + Arrays.toString(items) + "]";
  }
}
