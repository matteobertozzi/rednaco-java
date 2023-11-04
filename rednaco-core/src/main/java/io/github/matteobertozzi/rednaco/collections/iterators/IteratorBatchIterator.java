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

import java.util.Iterator;
import java.util.NoSuchElementException;

class IteratorBatchIterator<T> implements BatchIterator<T> {
  private final BatchIterator<T> iterator;

  IteratorBatchIterator(final int batchSize, final Iterator<T> iterator) {
    this.iterator = new BatchIterator<>(batchSize, iterator);
  }

  @Override
  public int batchSize() {
    return iterator.batchSize;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasMore();
  }

  @Override
  public Iterable<T> next() {
    iterator.skipToNextIndex();
    return iterator;
  }

  private static final class BatchIterator<T> implements Iterable<T>, Iterator<T> {
    private final Iterator<T> iterator;
    private final int batchSize;
    private int nextIndex;
    private int index;

    private BatchIterator(final int batchSize, final Iterator<T> iterator) {
      this.iterator = iterator;
      this.batchSize = batchSize;
      this.nextIndex = 0;
      this.index = 0;
    }

    public void skipToNextIndex() {
      while (index < nextIndex && iterator.hasNext()) {
        iterator.next();
        index++;
      }
      nextIndex += batchSize;
    }

    public boolean hasMore() {
      return iterator.hasNext();
    }

    @Override
    public boolean hasNext() {
      return index < nextIndex && iterator.hasNext();
    }

    @Override
    public T next() {
      if (index >= nextIndex) {
        throw new NoSuchElementException();
      }
      index++;
      return iterator.next();
    }

    @Override
    public Iterator<T> iterator() {
      return this;
    }
  }
}
