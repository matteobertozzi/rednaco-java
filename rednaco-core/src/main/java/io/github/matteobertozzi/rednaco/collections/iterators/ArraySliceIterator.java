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

public class ArraySliceIterator<T> implements Iterable<T>, PeekIterator<T> {
  private final T[] items;
  private final int endIndex;
  private int index;

  public ArraySliceIterator(final T[] items, final int off, final int len) {
    this.items = items;
    this.endIndex = off + len;
    this.index = off;
  }

  @Override
  public boolean hasNext() {
    return index < endIndex;
  }

  @Override
  public T next() {
    if (index >= endIndex) {
      throw new NoSuchElementException();
    }
    return items[index++];
  }

  @Override
  public T peek() {
    return (index < endIndex) ? items[index] : null;
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }
}
