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

package io.github.matteobertozzi.rednaco.collections.arrays;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestEnumArray {
  public enum TestItem { ITEM_A, ITEM_B, ITEM_C }

  @Test
  public void testEnumArray() {
    final EnumArray<TestItem> items = new EnumArray<>(TestItem.class);
    Assertions.assertEquals(0, items.size());

    items.add(TestItem.ITEM_A);
    Assertions.assertEquals(1, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));

    items.add(TestItem.ITEM_C);
    Assertions.assertEquals(2, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));

    items.add(TestItem.ITEM_B);
    Assertions.assertEquals(3, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(2));

    items.add(TestItem.ITEM_A);
    Assertions.assertEquals(4, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(2));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(3));

    items.add(TestItem.ITEM_A);
    Assertions.assertEquals(5, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(2));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(3));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(4));

    items.add(TestItem.ITEM_C);
    Assertions.assertEquals(6, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(2));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(3));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(4));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(5));

    items.add(TestItem.ITEM_B);
    Assertions.assertEquals(7, items.size());
    Assertions.assertEquals(TestItem.ITEM_A, items.get(0));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(1));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(2));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(3));
    Assertions.assertEquals(TestItem.ITEM_A, items.get(4));
    Assertions.assertEquals(TestItem.ITEM_C, items.get(5));
    Assertions.assertEquals(TestItem.ITEM_B, items.get(6));
  }

  @Test
  public void testRandEnumArray() {
    final TestItem[] enumItems = TestItem.values();
    final EnumArray<TestItem> items = new EnumArray<>(TestItem.class);

    final long seed = System.currentTimeMillis();
    final Random rand = new Random(seed);
    for (int i = 0; i < 10_000; ++i) {
      final int index = rand.nextInt(enumItems.length);
      items.add(enumItems[index]);
      Assertions.assertEquals(i + 1, items.size());

      final Random checkRand = new Random(seed);
      for (int j = 0; j <= i; ++j) {
        final int checkIndex = checkRand.nextInt(enumItems.length);
        Assertions.assertEquals(enumItems[checkIndex], items.get(j));
      }

      checkRand.setSeed(seed);
      items.forEach(v -> Assertions.assertEquals(enumItems[checkRand.nextInt(enumItems.length)], v));
    }
  }
}
