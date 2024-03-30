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

package io.github.matteobertozzi.rednaco.strings;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBase16 {
  @Test
  public void testBase16() {
    testEncodeDecode(Base16.base16(), "6c696768742077", "light w");
    testEncodeDecode(Base16.base16(), "6c6967687420776f", "light wo");
    testEncodeDecode(Base16.base16(), "6c6967687420776f72", "light wor");
    testEncodeDecode(Base16.base16(), "6c6967687420776f726b", "light work");
    testEncodeDecode(Base16.base16(), "6c6967687420776f726b2e", "light work.");
    testEncodeDecode(Base16.base16(), "68656c6c6f20776f726c64", "hello world");

    testRand(Base16.base16());
  }

  @Test
  public void testBase16Int() {
    testEncodeDecode(Base16.base16(), "000000ab", 0xAB);
    testEncodeDecode(Base16.base16(), "0000abcd", 0xABCD);
    testEncodeDecode(Base16.base16(), "00abcdef", 0xABCDEF);
    testEncodeDecode(Base16.base16(), "f7abcdef", 0xF7ABCDEF);
    testEncodeDecode(Base16.base16(), "ffabcdef", 0xFFABCDEF);
    testEncodeDecode(Base16.base16(), "00000000f7abcdef", 0xF7ABCDEFL);
    testEncodeDecode(Base16.base16(), "00000012f7abcdef", 0x12F7ABCDEFL);
    testEncodeDecode(Base16.base16(), "00003412f7abcdef", 0x3412F7ABCDEFL);
    testEncodeDecode(Base16.base16(), "00563412f7abcdef", 0x563412F7ABCDEFL);
    testEncodeDecode(Base16.base16(), "78563412f7abcdef", 0x78563412F7ABCDEFL);
    testEncodeDecode(Base16.base16(), "ff563412f7abcdef", 0xff563412F7ABCDEFL);
  }

  private static void testEncodeDecode(final Base16 base16, final String expected, final String input) {
    testEncodeDecode(base16, expected, input.getBytes(StandardCharsets.UTF_8));
    Assertions.assertEquals(input, new String(base16.decode(expected)));
  }

  private static void testEncodeDecode(final Base16 base16, final String expected, final byte[] input) {
    Assertions.assertEquals(expected, base16.encode(input));
    Assertions.assertArrayEquals(input, base16.decode(expected));
  }

  private static void testEncodeDecode(final Base16 base16, final String expected, final int i32) {
    Assertions.assertEquals(expected, base16.encodeInt32(i32));
    Assertions.assertEquals(i32, base16.decodeInt32(expected));
  }

  private static void testEncodeDecode(final Base16 base16, final String expected, final long i64) {
    Assertions.assertEquals(expected, base16.encodeInt64(i64));
    Assertions.assertEquals(i64, base16.decodeInt64(expected));
  }

  private static void testRand(final Base16 base16) {
    final Random rand = new Random();
    for (int i = 0; i < 100; ++i) {
      final byte[] data = new byte[rand.nextInt(0, 1 << 20)];
      rand.nextBytes(data);
      final String encBytes = base16.encode(data);
      Assertions.assertEquals(data.length * 2, encBytes.length());
      Assertions.assertArrayEquals(data, base16.decode(encBytes));
      Assertions.assertArrayEquals(data, base16.decode(encBytes.getBytes(StandardCharsets.UTF_8)));
    }
  }
}
