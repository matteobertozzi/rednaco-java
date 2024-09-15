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

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

public final class SetTestUtil {
  private SetTestUtil() {
    // no-op
  }

  public static <K> void testIndexedRandAddVerify(final int nItems, final Supplier<K> keySupplier) {
    final HashMap<K, Integer> refMap = new HashMap<>(nItems);
    final IndexedHashSet<K> set = new IndexedHashSet<>(8);
    while (set.size() < nItems) {
      final K key = keySupplier.get();
      if (set.contains(key)) {
        Assertions.assertEquals(refMap.get(key), set.addKey(key));
      } else {
        final int index = set.size();
        Assertions.assertEquals(index, set.addKey(key));
        Assertions.assertNull(refMap.put(key, index));
      }

      for (final Map.Entry<K, Integer> entry: refMap.entrySet()) {
        Assertions.assertEquals(entry.getValue(), set.getIndex(entry.getKey()));
      }
    }
  }

  public static <K> void testIndexedRandAddRemoveVerify(final int nItems, final Supplier<K> keySupplier) {
    final Random rand = new Random();
    final BitSet itemsBitmap = new BitSet(nItems);
    final HashMap<K, Integer> refMap = new HashMap<>(nItems);
    final IndexedHashSet<K> set = new IndexedHashSet<>(8);
    for (int k = 0; k < 50_000; ++k) {
      final K key = keySupplier.get();
      if (set.contains(key)) {
        Assertions.assertEquals(refMap.get(key), set.addKey(key));
      } else {
        final int index = set.addKey(key);
        Assertions.assertFalse(itemsBitmap.get(index));
        Assertions.assertNull(refMap.put(key, index));
        itemsBitmap.set(index);
      }

      if (rand.nextFloat() > 0.1) {
        int rmIndex = rand.nextInt(0, set.size());
        for (int i = 0; i < 10 && !itemsBitmap.get(rmIndex); ++i) {
          rmIndex = rand.nextInt(0, set.size());
        }

        if (itemsBitmap.get(rmIndex)) {
          final K rmKey = set.getAtIndex(rmIndex);
          Assertions.assertEquals(rmIndex, set.removeKey(rmKey));
          Assertions.assertEquals(rmIndex, refMap.remove(rmKey));
          itemsBitmap.clear(rmIndex);
        }
      }

      for (int i = 0; i < nItems; ++i) {
        Assertions.assertEquals(itemsBitmap.get(i), set.getAtIndex(i) != null);
      }

      for (final Map.Entry<K, Integer> entry: refMap.entrySet()) {
        Assertions.assertEquals(entry.getValue(), set.getIndex(entry.getKey()));
      }
    }
  }
}
