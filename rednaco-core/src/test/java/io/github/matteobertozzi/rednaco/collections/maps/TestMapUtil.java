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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMapUtil {
  public record TestItem(String sVal, int iVal) {}

  @Test
  public void testSize() {
    Assertions.assertEquals(0, MapUtil.size(null));
    Assertions.assertEquals(0, MapUtil.size(Map.of()));
    Assertions.assertEquals(1, MapUtil.size(Map.of("a", 1)));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(MapUtil.isEmpty(null));
    Assertions.assertTrue(MapUtil.isEmpty(Map.of()));
    Assertions.assertFalse(MapUtil.isEmpty(Map.of("a", 1)));

    Assertions.assertFalse(MapUtil.isNotEmpty(null));
    Assertions.assertFalse(MapUtil.isNotEmpty(Map.of()));
    Assertions.assertTrue(MapUtil.isNotEmpty(Map.of("a", 1)));
  }

  @Test
  public void testHashMapFrom() {
    final List<TestItem> items = List.of(new TestItem("aaa", 1), new TestItem("bbb", 2), new TestItem("ccc", 3));

    final Map<String, TestItem> itemsSKey = MapUtil.newHashMapFrom(items, TestItem::sVal);
    Assertions.assertEquals(3, itemsSKey.size());
    Assertions.assertSame(items.get(0), itemsSKey.get("aaa"));
    Assertions.assertSame(items.get(1), itemsSKey.get("bbb"));
    Assertions.assertSame(items.get(2), itemsSKey.get("ccc"));
    Assertions.assertNull(itemsSKey.get("ddd"));

    final Map<Integer, String> itemsMap = MapUtil.newHashMapFrom(items, TestItem::iVal, TestItem::sVal);
    Assertions.assertEquals(3, itemsMap.size());
    Assertions.assertEquals("aaa", itemsMap.get(1));
    Assertions.assertEquals("bbb", itemsMap.get(2));
    Assertions.assertEquals("ccc", itemsMap.get(3));
    Assertions.assertNull(itemsMap.get(4));

    final Map<String, Integer> itemsSKeyIVal = MapUtil.newHashMapFrom(itemsSKey, TestItem::iVal);
    Assertions.assertEquals(3, itemsSKeyIVal.size());
    Assertions.assertSame(1, itemsSKeyIVal.get("aaa"));
    Assertions.assertSame(2, itemsSKeyIVal.get("bbb"));
    Assertions.assertSame(3, itemsSKeyIVal.get("ccc"));
    Assertions.assertNull(itemsSKeyIVal.get("ddd"));

    final Map<TestItem, TestItem> itemItemMap = MapUtil.newHashMapFrom(items, Function.identity());
    Assertions.assertEquals(3, itemsSKey.size());
    Assertions.assertSame(items.get(0), itemItemMap.get(items.get(0)));
    Assertions.assertSame(items.get(1), itemItemMap.get(items.get(1)));
    Assertions.assertSame(items.get(2), itemItemMap.get(items.get(2)));

    final Map<String, TestItem> itemSKey2 = MapUtil.newHashMapFrom(itemItemMap, TestItem::sVal, Function.identity());
    Assertions.assertEquals(itemsSKey, itemSKey2);
    Assertions.assertEquals(3, itemsSKey.size());
    Assertions.assertSame(items.get(0), itemsSKey.get("aaa"));
    Assertions.assertSame(items.get(1), itemsSKey.get("bbb"));
    Assertions.assertSame(items.get(2), itemsSKey.get("ccc"));
    Assertions.assertNull(itemsSKey.get("ddd"));

    final Map<Integer, TestItem> itemIKey = MapUtil.newHashMapFrom(itemsSKey, (k, v) -> v.iVal(), Function.identity());
    Assertions.assertEquals(3, itemIKey.size());
    Assertions.assertSame(items.get(0), itemIKey.get(1));
    Assertions.assertSame(items.get(1), itemIKey.get(2));
    Assertions.assertSame(items.get(2), itemIKey.get(3));
    Assertions.assertNull(itemIKey.get(4));
  }
}
