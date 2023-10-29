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

public class TestBitUtil {
  @Test
  public void testAign() {
    Assertions.assertEquals(0, BitUtil.align(0, 8));
    Assertions.assertEquals(8, BitUtil.align(1, 8));
    Assertions.assertEquals(8, BitUtil.align(3, 8));
    Assertions.assertEquals(8, BitUtil.align(6, 8));
    Assertions.assertEquals(8, BitUtil.align(7, 8));
    Assertions.assertEquals(8, BitUtil.align(8, 8));
    Assertions.assertEquals(16, BitUtil.align(1, 16));
    Assertions.assertEquals(32, BitUtil.align(32, 16));

    Assertions.assertEquals(0L, BitUtil.align(0L, 4096));
    Assertions.assertEquals(4096L, BitUtil.align(2L, 4096));
    Assertions.assertEquals(1099511627776L, BitUtil.align(1099511627766L, 4096));
  }

  @Test
  public void testPow2() {
    Assertions.assertFalse(BitUtil.isPow2(0));
    Assertions.assertTrue(BitUtil.isPow2(2));
    Assertions.assertTrue(BitUtil.isPow2(4));
    Assertions.assertTrue(BitUtil.isPow2(8));
    Assertions.assertTrue(BitUtil.isPow2(16));
    Assertions.assertTrue(BitUtil.isPow2(32));
    Assertions.assertTrue(BitUtil.isPow2(128L));
    Assertions.assertTrue(BitUtil.isPow2(1L << 42));
    Assertions.assertTrue(BitUtil.isPow2(1L << 52));

    Assertions.assertFalse(BitUtil.isPow2(5));
    Assertions.assertFalse(BitUtil.isPow2(7));
    Assertions.assertFalse(BitUtil.isPow2(60));
    Assertions.assertFalse(BitUtil.isPow2((1L << 42) - 1));
    Assertions.assertFalse(BitUtil.isPow2((1L << 37) - 4));
  }

  @Test
  public void testNextPow2() {
    Assertions.assertEquals(1, BitUtil.nextPow2(0));
    Assertions.assertEquals(2, BitUtil.nextPow2(2));
    Assertions.assertEquals(8, BitUtil.nextPow2(5));
    Assertions.assertEquals(64, BitUtil.nextPow2(33));
    Assertions.assertEquals(128, BitUtil.nextPow2(127));

    Assertions.assertEquals(1L << 37, BitUtil.nextPow2((1L << 37) - 20));
    Assertions.assertEquals(1L << 62, BitUtil.nextPow2((1L << 62) - 123));
  }
}
