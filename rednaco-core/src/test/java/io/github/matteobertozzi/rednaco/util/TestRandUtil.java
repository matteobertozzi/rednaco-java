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

package io.github.matteobertozzi.rednaco.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.collections.arrays.ArraySearchUtil;

public class TestRandUtil {
  @Test
  public void testRandInt() {
    for (int i = 0; i < 1000; ++i) {
      final long v = RandData.generateInt(3, 15);
      Assertions.assertTrue(v >= 3 && v <= 15);
    }
  }

  @Test
  public void testRandLong() {
    for (int i = 0; i < 1000; ++i) {
      final long v = RandData.generateLong(3, 15);
      Assertions.assertTrue(v >= 3 && v <= 15);
    }
  }

  @Test
  public void testRandString() {
    final char[] charset = new char[] { 'f', 'g', 'h', 'i' };
    for (int i = 0; i < 1000; ++i) {
      final String v = RandData.generateString(i, charset);
      Assertions.assertEquals(i, v.length());
      for (int c = 0; c < i; ++c) {
        Assertions.assertFalse(ArraySearchUtil.indexOf(charset, v.charAt(c)) < 0);
      }
    }
  }

  @Test
  public void testRandChars() {
    final char[] charset = new char[] { 'f', 'g', 'h', 'i' };
    for (int i = 0; i < 1000; ++i) {
      final char[] v = RandData.generateChars(i, charset);
      Assertions.assertEquals(i, v.length);
      for (int c = 0; c < i; ++c) {
        Assertions.assertFalse(ArraySearchUtil.indexOf(charset, v[c]) < 0);
      }
    }
  }
}
