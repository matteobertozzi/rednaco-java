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

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestHashIndexedArrayMap {
  public record Entity(String key, Long value) {}

  @Test
  public void testSimple() {
    final String[] keys = new String[]   { "k1", "k2", "k3", "k4", "k5" };
    final Integer[] vals = new Integer[] {  10,   20,   30,   40,   50  };

    final HashIndexedArrayMap<String, Integer> map = new HashIndexedArrayMap<>(keys, vals);
    Assertions.assertEquals(keys.length, map.size());
    for (int i = 0; i < keys.length; ++i) {
      Assertions.assertTrue(map.containsKey(keys[i]));
      Assertions.assertTrue(map.containsValue(vals[i]));
      Assertions.assertEquals(i, map.getIndex(keys[i]));
      Assertions.assertEquals(vals[i], map.getAtIndex(i));
    }

    final Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
    for (int i = 0; it.hasNext(); ++i) {
      final Entry<String, Integer> entry = it.next();
      Assertions.assertEquals(keys[i], entry.getKey());
      Assertions.assertEquals(vals[i], entry.getValue());
    }
    Assertions.assertFalse(it.hasNext());
  }

  @Test
  public void testFromEntity() {
    final Entity[] entries = new Entity[] {
      new Entity("k1", 10L),
      new Entity("k2", 20L),
      new Entity("k3", 30L),
    };

    final HashIndexedArrayMap<String, Entity> map = HashIndexedArrayMap.fromEntity(entries, Entity::key);
    Assertions.assertEquals(entries.length, map.size());
    for (int i = 0; i < entries.length; ++i) {
      Assertions.assertTrue(map.containsKey(entries[i].key()));
      Assertions.assertTrue(map.containsValue(entries[i]));
      Assertions.assertEquals(i, map.getIndex(entries[i].key()));
      Assertions.assertEquals(entries[i], map.getAtIndex(i));
    }

    final Iterator<Entry<String, Entity>> it = map.entrySet().iterator();
    for (int i = 0; it.hasNext(); ++i) {
      final Entry<String, Entity> entry = it.next();
      Assertions.assertEquals(entries[i].key(), entry.getKey());
      Assertions.assertEquals(entries[i], entry.getValue());
    }
    Assertions.assertFalse(it.hasNext());
  }
}
