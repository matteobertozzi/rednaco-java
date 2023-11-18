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

public class TestBase32 {
  @Test
  public void testBase32() {
    testEncodeDecode(Base32.base32(), "NRUWO2DUEB3Q", "light w");
    testEncodeDecode(Base32.base32(), "NRUWO2DUEB3W6", "light wo");
    testEncodeDecode(Base32.base32(), "NRUWO2DUEB3W64Q", "light wor");
    testEncodeDecode(Base32.base32(), "NRUWO2DUEB3W64TL", "light work");
    testEncodeDecode(Base32.base32(), "NRUWO2DUEB3W64TLFY", "light work.");
    testEncodeDecode(Base32.base32(), "NBSWY3DPEB3W64TMMQ", "hello world");

    testRand(Base32.base32());
  }

  @Test
  public void testBase32Hex() {
    testEncodeDecode(Base32.base32hex(), "DHKMEQ3K41RG", "light w");
    testEncodeDecode(Base32.base32hex(), "DHKMEQ3K41RMU", "light wo");
    testEncodeDecode(Base32.base32hex(), "DHKMEQ3K41RMUSG", "light wor");
    testEncodeDecode(Base32.base32hex(), "DHKMEQ3K41RMUSJB", "light work");
    testEncodeDecode(Base32.base32hex(), "DHKMEQ3K41RMUSJB5O", "light work.");
    testEncodeDecode(Base32.base32hex(), "D1IMOR3F41RMUSJCCG", "hello world");

    testRand(Base32.base32hex());
  }

  @Test
  public void testBase32Crockford() {
    testEncodeDecode(Base32.crockfordBase32(), "DHMPET3M41VG", "light w");
    testEncodeDecode(Base32.crockfordBase32(), "DHMPET3M41VPY", "light wo");
    testEncodeDecode(Base32.crockfordBase32(), "DHMPET3M41VPYWG", "light wor");
    testEncodeDecode(Base32.crockfordBase32(), "DHMPET3M41VPYWKB", "light work");
    testEncodeDecode(Base32.crockfordBase32(), "DHMPET3M41VPYWKB5R", "light work.");
    testEncodeDecode(Base32.crockfordBase32(), "D1JPRV3F41VPYWKCCG", "hello world");

    testRand(Base32.crockfordBase32());
  }

  @Test
  public void testZBase32() {
    testEncodeDecode(Base32.zbase32(), "ptwsq4dwrb5o", "light w");
    testEncodeDecode(Base32.zbase32(), "ptwsq4dwrb5s6", "light wo");
    testEncodeDecode(Base32.zbase32(), "ptwsq4dwrb5s6ho", "light wor");
    testEncodeDecode(Base32.zbase32(), "ptwsq4dwrb5s6hum", "light work");
    testEncodeDecode(Base32.zbase32(), "ptwsq4dwrb5s6humfa", "light work.");
    testEncodeDecode(Base32.zbase32(), "pb1sa5dxrb5s6hucco", "hello world");

    testRand(Base32.zbase32());
  }

  private static void testEncodeDecode(final Base32 base32, final String expected, final String input) {
    Assertions.assertEquals(expected, base32.encode(input.getBytes(StandardCharsets.UTF_8)));
    Assertions.assertEquals(input, new String(base32.decode(expected.getBytes(StandardCharsets.UTF_8))));
    Assertions.assertEquals(input, new String(base32.decode(expected)));
  }

  private static void testRand(final Base32 base32) {
    final Random rand = new Random();
    for (int i = 0; i < 100; ++i) {
      final byte[] data = new byte[rand.nextInt(0, 1 << 20)];
      rand.nextBytes(data);
      final String encBytes = base32.encode(data);
      Assertions.assertArrayEquals(data, base32.decode(encBytes));
      Assertions.assertArrayEquals(data, base32.decode(encBytes.getBytes(StandardCharsets.UTF_8)));
    }
  }
}
