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

package io.github.matteobertozzi.rednaco.iterators;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.iterators.PeekIterator;
import io.github.matteobertozzi.rednaco.collections.iterators.SimplePeekIterator;

public class TestSimplePeekIterator {
  @Test
  public void testPeekIterator() {
    final List<String> items = List.of("ddd", "aaa", "ccc", "bbb");
    final PeekIterator<String> it = SimplePeekIterator.from(items.iterator());
    for (final String item: items) {
      Assertions.assertTrue(it.hasNext());
      Assertions.assertEquals(item, it.peek());
      Assertions.assertEquals(item, it.next());
    }
    Assertions.assertFalse(it.hasNext());
    Assertions.assertThrowsExactly(NoSuchElementException.class, it::peek);
  }
}
