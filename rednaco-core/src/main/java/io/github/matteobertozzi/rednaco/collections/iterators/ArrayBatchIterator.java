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

class ArrayBatchIterator<T> implements BatchIterator<T> {
  private final T[] items;
  private final int batchSize;
  private int nextOffset;

  ArrayBatchIterator(final int batchSize, final T[] items) {
    this.items = items;
    this.batchSize = batchSize;
    this.nextOffset = 0;
  }

  @Override
  public int batchSize() {
    return batchSize;
  }

  @Override
  public boolean hasNext() {
    return nextOffset < items.length;
  }

  @Override
  public Iterable<T> next() {
    if (nextOffset >= items.length) {
      throw new NoSuchElementException();
    }
    final int offset = nextOffset;
    final int length = Math.min(batchSize, items.length - nextOffset);
    nextOffset += batchSize;
    return new ArraySliceIterator<>(items, offset, length);
  }
}
