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

package io.github.matteobertozzi.rednaco.collections.sets;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.TestItemBadHashCode;

public class TestIndexedHashSet {
  @Test
  public void testTestItemBadHashCode() {
    final TestItemBadHashCode[] items = new TestItemBadHashCode[100];
    final IndexedHashSet<TestItemBadHashCode> set = new IndexedHashSet<>(8);
    for (int i = 0; i < items.length; ++i) {
      items[i] = new TestItemBadHashCode(i);
      set.addKey(items[i]);
    }
    verifyItems(set, items);

    Assertions.assertEquals(3, set.removeKey(items[3]));
    Assertions.assertEquals(99, set.size());
    Assertions.assertEquals(-1, set.getIndex(items[3]));
    Assertions.assertNull(set.getAtIndex(3));
    items[3] = null;
    verifyItems(set, items);

    Assertions.assertEquals(99, set.removeKey(items[99]));
    Assertions.assertEquals(98, set.size());
    Assertions.assertEquals(-1, set.getIndex(items[99]));
    Assertions.assertNull(set.getAtIndex(99));
    items[99] = null;
    verifyItems(set, items);

    items[99] = new TestItemBadHashCode(199);
    Assertions.assertEquals(99, set.addKey(items[99]));
    Assertions.assertEquals(99, set.size());
    Assertions.assertEquals(99, set.getIndex(items[99]));
    Assertions.assertEquals(items[99], set.getAtIndex(99));
    verifyItems(set, items);
  }

  @Test
  public void testSimple() {
    final IndexedHashSet<String> set = new IndexedHashSet<>(8);
    Assertions.assertTrue(set.isEmpty());
    for (int i = 0; i < 100; ++i) {
      Assertions.assertEquals(i, set.addKey("k" + i));
      Assertions.assertEquals(i + 1, set.size());
      Assertions.assertEquals(i, set.getIndex("k" + i));
      Assertions.assertEquals("k" + i, set.getAtIndex(i));
    }
    Assertions.assertEquals(3, set.removeKey("k3"));
    Assertions.assertEquals(99, set.size());
    Assertions.assertEquals(-1, set.getIndex("k3"));
    Assertions.assertNull(set.getAtIndex(3));

    Assertions.assertEquals(87, set.removeKey("k87"));
    Assertions.assertEquals(98, set.size());
    Assertions.assertEquals(-1, set.getIndex("k87"));
    Assertions.assertNull(set.getAtIndex(87));

    Assertions.assertEquals(87, set.addKey("k187"));
    Assertions.assertEquals(99, set.size());
    Assertions.assertEquals(87, set.getIndex("k187"));
    Assertions.assertEquals("k187", set.getAtIndex(87));

    Assertions.assertEquals(3, set.addKey("k103"));
    Assertions.assertEquals(100, set.size());
    Assertions.assertEquals(3, set.getIndex("k103"));
    Assertions.assertEquals("k103", set.getAtIndex(3));
  }

  @Test
  public void testRandAddVerify() {
    final int NITEMS = 2_000;
    final Random rand = new Random();
    SetTestUtil.testIndexedRandAddVerify(NITEMS, () -> "k" + rand.nextInt(0, NITEMS));
  }

  @Test
  public void testRandAddRemoveVerify() {
    final int NITEMS = 2_000;
    final Random rand = new Random();
    SetTestUtil.testIndexedRandAddRemoveVerify(NITEMS, () -> "k" + rand.nextInt(0, NITEMS));
  }

  private static <T> void verifyItems(final IndexedHashSet<T> set, final T[] items) {
    for (int i = 0; i < items.length; ++i) {
      if (items[i] == null) continue;
      Assertions.assertEquals(i, set.getIndex(items[i]));
      Assertions.assertEquals(items[i], set.getAtIndex(i));
    }
  }
}
