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

public class TestObjectArray {
  public record TestItem(long value) {}

  @Test
  public void testRandAdd() {
    final ObjectArray<TestItem> items = new ObjectArray<>(TestItem.class, 256);
    Assertions.assertEquals(0, items.size());
    Assertions.assertTrue(items.isEmpty());
    Assertions.assertFalse(items.isNotEmpty());

    final long seed = System.currentTimeMillis();
    final Random rand = new Random(seed);
    for (int i = 0; i < 10_000; ++i) {
      items.add(new TestItem(rand.nextLong()));
      Assertions.assertEquals(i + 1, items.size());
      Assertions.assertFalse(items.isEmpty());
      Assertions.assertTrue(items.isNotEmpty());

      // verify get
      final Random checkRand = new Random(seed);
      for (int j = 0; j <= i; ++j) {
        Assertions.assertEquals(checkRand.nextLong(), items.get(j).value());
      }

      // verify forEach() single item
      checkRand.setSeed(seed);
      items.forEach(v -> Assertions.assertEquals(checkRand.nextLong(), v.value()));

      // verify forEach() batch
      checkRand.setSeed(seed);
      items.forEach((arr, arrOff, arrLen) -> {
        for (int j = 0; j < arrLen; ++j) {
          Assertions.assertEquals(checkRand.nextLong(), arr[arrOff + j].value());
        }
      });
    }
  }
}
