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

public class TestBase64 {
  @Test
  public void testBase64() {
    testEncodeDecode(Base64.base64(), "bGlnaHQgdw", "light w");
    testEncodeDecode(Base64.base64(), "bGlnaHQgd28", "light wo");
    testEncodeDecode(Base64.base64(), "bGlnaHQgd29y", "light wor");
    testEncodeDecode(Base64.base64(), "bGlnaHQgd29yaw", "light work");
    testEncodeDecode(Base64.base64(), "bGlnaHQgd29yay4", "light work.");
    testEncodeDecode(Base64.base64(), "aGVsbG8gd29ybGQ", "hello world");

    testRand(Base64.base64());
  }

  @Test
  public void testBase64Hex() {
    testEncodeDecode(Base64.base64hex(), "Q5_bP6FVSk", "light w");
    testEncodeDecode(Base64.base64hex(), "Q5_bP6FVSqw", "light wo");
    testEncodeDecode(Base64.base64hex(), "Q5_bP6FVSqxm", "light wor");
    testEncodeDecode(Base64.base64hex(), "Q5_bP6FVSqxmPk", "light work");
    testEncodeDecode(Base64.base64hex(), "Q5_bP6FVSqxmPms", "light work.");
    testEncodeDecode(Base64.base64hex(), "P5KgQ5wVSqxmQ5F", "hello world");

    testRand(Base64.base64hex());
  }

  private static void testEncodeDecode(final Base64 base64, final String expected, final String input) {
    Assertions.assertEquals(expected, base64.encode(input.getBytes(StandardCharsets.UTF_8)));
    Assertions.assertEquals(input, new String(base64.decode(expected.getBytes(StandardCharsets.UTF_8))));
    Assertions.assertEquals(input, new String(base64.decode(expected)));
  }

  private static void testRand(final Base64 base64) {
    final Random rand = new Random();
    for (int i = 0; i < 100; ++i) {
      final byte[] data = new byte[rand.nextInt(0, 1 << 20)];
      rand.nextBytes(data);
      final String encBytes = base64.encode(data);
      Assertions.assertArrayEquals(data, base64.decode(encBytes));
      Assertions.assertArrayEquals(data, base64.decode(encBytes.getBytes(StandardCharsets.UTF_8)));
    }
  }
}
