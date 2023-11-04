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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.iterators.FilteredIterator;

public class TestFilteredIterator {
  @Test
  public void testFilteredIterator() {
    final List<Integer> items = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

    final FilteredIterator<Integer> it = new FilteredIterator<>(items.iterator(), v -> v % 2 == 0);
    int count = 0;
    while (it.hasNext()) {
      Assertions.assertTrue(it.hasNext());
      Assertions.assertEquals(items.get(count * 2), it.peek());
      final int value = it.next();
      Assertions.assertEquals(items.get(count * 2).intValue(), value);
      Assertions.assertEquals(0, value % 2);
      count++;
    }
    Assertions.assertFalse(it.hasNext());
    Assertions.assertEquals(5, count);
  }
}
