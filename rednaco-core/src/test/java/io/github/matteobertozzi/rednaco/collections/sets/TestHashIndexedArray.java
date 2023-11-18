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

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestHashIndexedArray {
  @Test
  public void testSimple() {
    final Integer[] in = new Integer[] { 1, 2, 3, 4, 5};
    final Integer[] out = new Integer[] { 11, 12, 13, 14, 15 };
    final HashIndexedArray<Integer> index = new HashIndexedArray<>(in);
    for (int i = 0; i < in.length; ++i) {
      Assertions.assertTrue(index.contains(in[i]));
      Assertions.assertEquals(in[i], index.get(i));
      Assertions.assertEquals(i, index.getIndex(in[i]));
    }
    for (int i = 0; i < out.length; ++i) {
      Assertions.assertFalse(index.contains(out[i]));
      Assertions.assertEquals(-1, index.getIndex(out[i]));
    }
  }

  @Test
  public void testRand() {
    final Random rand = new Random();
    final String[] keys = new String[rand.nextInt(0, 1 << 20)];
    for (int i = 0; i < keys.length; ++i) {
      keys[i] = "k" + i;
    }

    final HashIndexedArray<String> index = new HashIndexedArray<>(keys);
    for (int i = 0; i < keys.length; ++i) {
      Assertions.assertTrue(index.contains(keys[i]));
      Assertions.assertEquals(keys[i], index.get(i));
      Assertions.assertEquals(i, index.getIndex(keys[i]));
    }
  }
}
