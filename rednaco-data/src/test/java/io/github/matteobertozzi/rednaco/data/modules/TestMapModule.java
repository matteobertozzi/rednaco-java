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

package io.github.matteobertozzi.rednaco.data.modules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.maps.HashIndexedArrayMap;
import io.github.matteobertozzi.rednaco.collections.sets.HashIndexedArray;
import io.github.matteobertozzi.rednaco.data.JsonFormat;

public class TestMapModule {
  public record TestEntity(String a, long b) {}
  public record TestRec(HashIndexedArray<String> index, HashIndexedArrayMap<String, TestEntity> map) {}

  @Test
  public void testHashIndexedArray() {
    final HashIndexedArray<String> index = new HashIndexedArray<>(new String[] { "a", "b", "c" });
    Assertions.assertEquals("[\"a\",\"b\",\"c\"]", JsonFormat.INSTANCE.asString(index));
  }

  @Test
  public void testRecord() {
    final TestEntity[] entries = new TestEntity[] { new TestEntity("k1", 10), new TestEntity("k2", 20), new TestEntity("k3", 30) };
    final HashIndexedArray<String> index = new HashIndexedArray<>(new String[] { "a", "b", "c" });
    final HashIndexedArrayMap<String, TestEntity> map = HashIndexedArrayMap.fromEntry(entries, TestEntity::a);

    final TestRec rec = new TestRec(index, map);
    final String json = JsonFormat.INSTANCE.asString(rec);
    System.out.println(rec);
    System.out.println("JSON: " + json);
  }
}
