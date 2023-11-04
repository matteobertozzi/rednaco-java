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

package io.github.matteobertozzi.rednaco.bytes.encoding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.util.RandData;

public class TestIntUtil {
  @Test
  public void testWidth() {
    Assertions.assertEquals(0, IntUtil.getWidth(0));
    Assertions.assertEquals(1, IntUtil.getWidth(1));
    Assertions.assertEquals(2, IntUtil.getWidth(2));
    Assertions.assertEquals(2, IntUtil.getWidth(3));
    Assertions.assertEquals(2, IntUtil.getWidth(3));

    Assertions.assertEquals(32, IntUtil.getWidth(-1));
    for (int i = 0; i < 31; ++i) {
      Assertions.assertEquals(i, IntUtil.getWidth((1 << i) - 1));
    }

    Assertions.assertEquals(64, IntUtil.getWidth(-1L));
    for (int i = 0; i < 63; ++i) {
      Assertions.assertEquals(i, IntUtil.getWidth((1L << i) - 1));
    }
  }

  @Test
  public void testSize() {
    Assertions.assertEquals(1, IntUtil.size(0));
    Assertions.assertEquals(1, IntUtil.size(1));
    Assertions.assertEquals(1, IntUtil.size(127));
    Assertions.assertEquals(1, IntUtil.size(250));
    Assertions.assertEquals(2, IntUtil.size(256));

    Assertions.assertEquals(1, IntUtil.size(0xf));
    Assertions.assertEquals(1, IntUtil.size(0xff));
    Assertions.assertEquals(2, IntUtil.size(0xfff));
    Assertions.assertEquals(2, IntUtil.size(0xffff));
    Assertions.assertEquals(3, IntUtil.size(0xfffff));
    Assertions.assertEquals(3, IntUtil.size(0xffffff));
    Assertions.assertEquals(4, IntUtil.size(0xfffffff));
    Assertions.assertEquals(4, IntUtil.size(0xffffffff));
    Assertions.assertEquals(5, IntUtil.size(0xfffffffffL));
    Assertions.assertEquals(5, IntUtil.size(0xffffffffffL));
    Assertions.assertEquals(6, IntUtil.size(0xfffffffffffL));
    Assertions.assertEquals(6, IntUtil.size(0xffffffffffffL));
    Assertions.assertEquals(7, IntUtil.size(0xfffffffffffffL));
    Assertions.assertEquals(7, IntUtil.size(0xffffffffffffffL));
    Assertions.assertEquals(8, IntUtil.size(0xfffffffffffffffL));
    Assertions.assertEquals(8, IntUtil.size(0xffffffffffffffffL));
  }

  @Test
  public void testZigZagInt() {
    for (int i = -(1 << 30); i < (1 << 30); ++i) {
      Assertions.assertEquals(i, IntUtil.zigZagDecode(IntUtil.zigZagEncode(i)));
    }
  }

  @Test
  public void testZigZagLong() {
    for (long i = -(1L << 32); i < (1L << 32); ++i) {
      Assertions.assertEquals(i, IntUtil.zigZagDecode(IntUtil.zigZagEncode(i)));
    }

    for (int i = 0; i < 1_000_000; ++i) {
      final long lValue = RandData.generateLong(-(1L << 62), 1L << 62);
      Assertions.assertEquals(lValue, IntUtil.zigZagDecode(IntUtil.zigZagEncode(lValue)));
    }
  }
}
