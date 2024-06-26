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

public class LimitedIterator<T> implements Iterable<T>, Iterator<T> {
  private final Iterator<T> iterator;
  private int avail;

  public LimitedIterator(final int limit, final Iterator<T> iterator) {
    this.iterator = iterator;
    this.avail = limit;
  }

  @Override
  public boolean hasNext() {
    return avail > 0 && iterator.hasNext();
  }

  @Override
  public T next() {
    if (avail == 0) {
      throw new NoSuchElementException();
    }

    avail--;
    return iterator.next();
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }
}
