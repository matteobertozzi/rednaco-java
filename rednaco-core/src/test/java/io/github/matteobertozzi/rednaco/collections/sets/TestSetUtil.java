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

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSetUtil {
  public record TestItem(String sVal, int iVal) {}

  @Test
  public void testSize() {
    Assertions.assertEquals(0, SetUtil.size(null));
    Assertions.assertEquals(0, SetUtil.size(Set.of()));
    Assertions.assertEquals(1, SetUtil.size(Set.of("a")));
    Assertions.assertEquals(2, SetUtil.size(Set.of("a", "b")));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(SetUtil.isEmpty(null));
    Assertions.assertTrue(SetUtil.isEmpty(Set.of()));
    Assertions.assertFalse(SetUtil.isEmpty(Set.of("a", 1)));

    Assertions.assertFalse(SetUtil.isNotEmpty(null));
    Assertions.assertFalse(SetUtil.isNotEmpty(Set.of()));
    Assertions.assertTrue(SetUtil.isNotEmpty(Set.of("a", 1)));
  }

  @Test
  public void testHashMapFrom() {
    final List<TestItem> items = List.of(new TestItem("aaa", 1), new TestItem("bbb", 2), new TestItem("ccc", 3));

    final Set<String> itemsSKey = SetUtil.newHashSetFrom(items, TestItem::sVal);
    Assertions.assertEquals(3, itemsSKey.size());
    Assertions.assertTrue(itemsSKey.contains("aaa"));
    Assertions.assertTrue(itemsSKey.contains("bbb"));
    Assertions.assertTrue(itemsSKey.contains("ccc"));
    Assertions.assertFalse(itemsSKey.contains("ddd"));
  }
}
