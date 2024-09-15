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

package io.github.matteobertozzi.rednaco.collections.maps;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;

public final class MapTestUtil {
  private MapTestUtil() {
    // no-op
  }

  public static <K, V> void testIndexedRandAddVerify(final int nItems, final Supplier<K> keySupplier, final Supplier<V> valueSupplier) {
    final V[] refValues = ArrayUtil.newArray(nItems);
    final HashMap<K, Integer> refIndexMap = new HashMap<>(nItems);
    final IndexedHashMap<K, V> map = new IndexedHashMap<>(8);
    while (map.size() < nItems) {
      final K key = keySupplier.get();
      if (map.containsKey(key)) {
        Assertions.assertTrue(refIndexMap.containsKey(key));
        final int expectedIndex = refIndexMap.get(key);
        final V expectedValue = refValues[expectedIndex];
        Assertions.assertEquals(expectedIndex, map.add(key, expectedValue));
        Assertions.assertEquals(expectedValue, map.get(key));
        Assertions.assertEquals(expectedIndex, map.addIfAbsent(key, valueSupplier.get()));
        Assertions.assertEquals(expectedValue, map.get(key));
      } else {
        final int index = map.size();
        refValues[index] = valueSupplier.get();
        Assertions.assertEquals(index, map.add(key, refValues[index]));
        Assertions.assertNull(refIndexMap.put(key, index));
      }

      verifyIndexedMap(map, refIndexMap, refValues);
    }
  }

  private static <K, V> void verifyIndexedMap(final IndexedHashMap<K, V> map, final Map<K, Integer> refIndexMap, final V[] refValues) {
    for (final Map.Entry<K, Integer> entry: refIndexMap.entrySet()) {
      final int expectedIndex = entry.getValue();
      final V expectedValue = refValues[expectedIndex];
      Assertions.assertEquals(entry.getKey(), map.getKeyAtIndex(expectedIndex));
      Assertions.assertEquals(expectedIndex, map.getIndex(entry.getKey()));
      Assertions.assertEquals(expectedValue, map.get(entry.getKey()));
      Assertions.assertEquals(expectedValue, map.getValueAtIndex(expectedIndex));
    }
  }

  public static <K, V> void testIndexedRandAddRemoveVerify(final int nItems, final Supplier<K> keySupplier, final Supplier<V> valueSupplier) {
    final Random rand = new Random();
    final BitSet itemsBitmap = new BitSet(nItems);
    final V[] refValues = ArrayUtil.newArray(nItems);
    final HashMap<K, Integer> refIndexMap = new HashMap<>(nItems);
    final IndexedHashMap<K, V> map = new IndexedHashMap<>(8);
    for (int k = 0; k < 50_000; ++k) {
      final K key = keySupplier.get();
      if (map.containsKey(key)) {
        Assertions.assertTrue(refIndexMap.containsKey(key));
        final int expectedIndex = refIndexMap.get(key);
        final V expectedValue = refValues[expectedIndex];
        Assertions.assertEquals(expectedIndex, map.add(key, expectedValue));
        Assertions.assertEquals(expectedValue, map.get(key));
        Assertions.assertEquals(expectedIndex, map.addIfAbsent(key, valueSupplier.get()));
        Assertions.assertEquals(expectedValue, map.get(key));
      } else {
        final V value = valueSupplier.get();
        final int index = map.add(key, value);
        Assertions.assertNull(refIndexMap.put(key, index));
        refValues[index] = value;
        itemsBitmap.set(index);
      }

      if (rand.nextFloat() > 0.1) {
        int rmIndex = rand.nextInt(0, map.size());
        for (int i = 0; i < 10 && !itemsBitmap.get(rmIndex); ++i) {
          rmIndex = rand.nextInt(0, map.size());
        }

        if (itemsBitmap.get(rmIndex)) {
          final K rmKey = map.getKeyAtIndex(rmIndex);
          Assertions.assertEquals(rmIndex, map.removeKey(rmKey));
          Assertions.assertEquals(rmIndex, refIndexMap.remove(rmKey));
          refValues[rmIndex] = null;
          itemsBitmap.clear(rmIndex);
        }
      }

      for (int i = 0; i < refValues.length; ++i) {
        Assertions.assertEquals(itemsBitmap.get(i), refValues[i] != null);
      }

      verifyIndexedMap(map, refIndexMap, refValues);
    }
  }
}
