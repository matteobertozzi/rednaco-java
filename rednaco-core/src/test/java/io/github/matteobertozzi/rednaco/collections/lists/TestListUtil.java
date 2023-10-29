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

package io.github.matteobertozzi.rednaco.collections.lists;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestListUtil {
  public record TestItem(String sVal, int iVal) {}

  // ================================================================================
  //  length related
  // ================================================================================
  @Test
  public void testLength() {
    Assertions.assertEquals(0, ListUtil.size(null));
    Assertions.assertEquals(0, ListUtil.size(List.of()));
    Assertions.assertEquals(3, ListUtil.size(List.of("a", "b", "c")));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(ListUtil.isEmpty(null));
    Assertions.assertTrue(ListUtil.isEmpty(List.of()));

    Assertions.assertFalse(ListUtil.isNotEmpty(null));
    Assertions.assertFalse(ListUtil.isNotEmpty(List.of()));

    Assertions.assertFalse(ListUtil.isEmpty(List.of("a", "b", "c")));
    Assertions.assertTrue(ListUtil.isNotEmpty(List.of("a", "b", "c")));
  }

  // ================================================================================
  //  New list related
  // ================================================================================
  @Test
  public void testEmptyIfNull() {
    final ArrayList<String> emptyList = new ArrayList<>();
    final ArrayList<String> notEmptyList = new ArrayList<>(List.of("a", "b", "c"));

    Assertions.assertEquals(List.of(), ListUtil.emptyIfNull(null));
    Assertions.assertSame(emptyList, ListUtil.emptyIfNull(emptyList));
    Assertions.assertSame(notEmptyList, ListUtil.emptyIfNull(notEmptyList));
  }

  @Test
  public void testNewArrayListIfNull() {
    final ArrayList<String> emptyList = new ArrayList<>();
    final ArrayList<String> notEmptyList = new ArrayList<>(List.of("a", "b", "c"));

    final List<String> newList = ListUtil.newArrayList(null);
    Assertions.assertEquals(0, newList.size());
    Assertions.assertEquals(ArrayList.class, newList.getClass());

    List<String> retList = ListUtil.newArrayList(notEmptyList);
    Assertions.assertEquals(3, retList.size());

    retList = ListUtil.newArrayList(emptyList);
    Assertions.assertEquals(0, retList.size());
  }

  @Test
  public void testFrom() {
    final List<TestItem> items = List.of(new TestItem("aaa", 1), new TestItem("bbb", 2), new TestItem("ccc", 3));
    final List<String> keys = ListUtil.newArrayListFrom(items, TestItem::sVal);
    Assertions.assertEquals(3, keys.size());
    Assertions.assertEquals("aaa", keys.get(0));
    Assertions.assertEquals("bbb", keys.get(1));
    Assertions.assertEquals("ccc", keys.get(2));
  }
}
