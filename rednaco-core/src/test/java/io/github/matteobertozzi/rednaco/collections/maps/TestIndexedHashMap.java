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
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestIndexedHashMap {

  @Test
  public void testRandAddVerify() {
    final int NITEMS = 2_000;
    final Random rand = new Random();
    MapTestUtil.testIndexedRandAddVerify(NITEMS, () -> "k" + rand.nextInt(0, NITEMS), () -> "v" + rand.nextLong());
  }

  @Test
  public void testRandAddRemoveVerify() {
    final int NITEMS = 2_000;
    final Random rand = new Random();
    MapTestUtil.testIndexedRandAddRemoveVerify(NITEMS, () -> "k" + rand.nextInt(0, NITEMS), () -> "v" + rand.nextLong());
  }

  @Test
  public void testIterator() {
    final IndexedHashMap<String, String> map = new IndexedHashMap<>(8);
    Iterator<Map.Entry<String, String>> it;

    it = map.entrySet().iterator();
    Assertions.assertFalse(it.hasNext());

    map.add("A", "1");
    it = map.entrySet().iterator();
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("A", "1"), it.next());

    map.add("B", "2");
    it = map.entrySet().iterator();
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("A", "1"), it.next());
    Assertions.assertEquals(Map.entry("B", "2"), it.next());

    map.add("C", "3");
    it = map.entrySet().iterator();
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("A", "1"), it.next());
    Assertions.assertEquals(Map.entry("B", "2"), it.next());
    Assertions.assertEquals(Map.entry("C", "3"), it.next());

    it = map.entrySet().iterator();
    Assertions.assertEquals(3, map.size());
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("A", "1"), it.next());
    it.remove();
    Assertions.assertEquals(2, map.size());
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("B", "2"), it.next());
    it.remove();
    Assertions.assertEquals(1, map.size());
    Assertions.assertTrue(it.hasNext());
    Assertions.assertEquals(Map.entry("C", "3"), it.next());
    it.remove();
    Assertions.assertFalse(it.hasNext());
    Assertions.assertEquals(0, map.size());
  }
}
