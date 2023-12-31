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

public class SimplePeekIterator<T> implements PeekIterator<T> {
  private final Iterator<? extends T> iterator;
  private boolean hasItem;
  private T item;

  private SimplePeekIterator(final Iterator<? extends T> iterator) {
    this.iterator = iterator;
  }

  @SuppressWarnings("unchecked")
  public static <T> PeekIterator<T> from(final Iterator<? extends T> iterator) {
    if (iterator instanceof PeekIterator) {
      return (PeekIterator<T>)iterator;
    }
    return new SimplePeekIterator<>(iterator);
  }

  @Override
  public boolean hasNext() {
    return hasItem || iterator.hasNext();
  }

  @Override
  public T next() {
    if (!hasItem) {
      return iterator.next();
    }

    final T result = this.item;
    this.hasItem = false;
    this.item = null;
    return result;
  }

  @Override
  public T peek() {
    if (!hasItem) {
      this.item = iterator.next();
      this.hasItem = true;
    }
    return item;
  }

  @Override
  public void remove() {
    iterator.remove();
  }
}
