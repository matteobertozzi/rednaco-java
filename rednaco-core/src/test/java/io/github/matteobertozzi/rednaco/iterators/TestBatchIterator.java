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

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.iterators.BatchIterator;

public class TestBatchIterator {
  private final String[] TEST_ITEMS = new String[] {
    "aaa", "bbb", "ccc",
    "ddd", "eee", "fff",
    "ggg", "hhh", "iii",
    "jjj"
  };
  private final List<List<String>> TEST_GROUPS = List.of(
    List.of("aaa", "bbb", "ccc"),
    List.of("ddd", "eee", "fff"),
    List.of("ggg", "hhh", "iii"),
    List.of("jjj")
  );

  @Test
  public void testBatchArrayIterator() {
    final BatchIterator<String> it = BatchIterator.of(3, TEST_ITEMS);
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIterator(it, TEST_GROUPS);
  }

  @Test
  public void testBatchArrayIteratorFirstItem() {
    final BatchIterator<String> it = BatchIterator.of(3, TEST_ITEMS);
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIteratorFirstItem(it, TEST_GROUPS);
  }

  @Test
  public void testBatchListIterator() {
    final BatchIterator<String> it = BatchIterator.of(3, List.of(TEST_ITEMS));
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIterator(it, TEST_GROUPS);
  }

  @Test
  public void testBatchListIteratorFirstItem() {
    final BatchIterator<String> it = BatchIterator.of(3, List.of(TEST_ITEMS));
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIteratorFirstItem(it, TEST_GROUPS);
  }

  @Test
  public void testBatchIteratorIterator() {
    final BatchIterator<String> it = BatchIterator.of(3, List.of(TEST_ITEMS).iterator());
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIterator(it, TEST_GROUPS);
  }

  @Test
  public void testBatchIteratorIteratorFirstItem() {
    final BatchIterator<String> it = BatchIterator.of(3, List.of(TEST_ITEMS).iterator());
    Assertions.assertEquals(3, it.batchSize());
    verifyBatchIteratorFirstItem(it, TEST_GROUPS);
  }

  private static <T> void verifyBatchIterator(final BatchIterator<T> it, final List<List<T>> groups) {
    for (final List<T> groupItems: groups) {
      Assertions.assertTrue(it.hasNext());
      Assertions.assertTrue(groupItems.size() <= it.batchSize());
      final Iterator<T> groupIt = it.next().iterator();
      for (final T item: groupItems) {
        Assertions.assertTrue(groupIt.hasNext());
        Assertions.assertEquals(item, groupIt.next());
      }
      Assertions.assertFalse(groupIt.hasNext());
    }
    Assertions.assertFalse(it.hasNext());
  }

  private static <T> void verifyBatchIteratorFirstItem(final BatchIterator<T> it, final List<List<T>> groups) {
    for (final List<T> groupItems: groups) {
      Assertions.assertTrue(it.hasNext());
      Assertions.assertTrue(groupItems.size() <= it.batchSize());
      final Iterator<T> groupIt = it.next().iterator();
      for (final T item: groupItems) {
        Assertions.assertTrue(groupIt.hasNext());
        Assertions.assertEquals(item, groupIt.next());
        break;
      }
      Assertions.assertEquals(groupItems.size() > 1, groupIt.hasNext());
    }
    Assertions.assertFalse(it.hasNext());
  }
}