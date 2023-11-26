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

package io.github.matteobertozzi.rednaco.collections.iterators;

import java.util.NoSuchElementException;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;

public class NonNullArrayIterator<T> implements PeekIterator<T> {
  private final T[] array;
  private final int offset;
  private final int length;
  private int nextIndex;

  public NonNullArrayIterator(final T[] array) {
    this(array, 0, ArrayUtil.length(array));
  }

  public NonNullArrayIterator(final T[] array, final int offset, final int length) {
    this.array = array;
    this.offset = offset;
    this.length = length;
    this.nextIndex = 0;
    skipNulls();
  }

  @Override
  public boolean hasNext() {
    return nextIndex < length;
  }

  @Override
  public T next() {
    if (nextIndex >= length) {
      throw new NoSuchElementException();
    }
    final T value = array[offset + nextIndex++];
    skipNulls();
    return value;
  }

  @Override
  public T peek() {
    if (nextIndex < length) {
      return array[offset + nextIndex];
    }
    return null;
  }

  private void skipNulls() {
    while (nextIndex < length && array[offset + nextIndex] == null) {
      nextIndex++;
    }
  }
}
