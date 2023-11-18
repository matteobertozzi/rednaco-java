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

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;

public class TestBaseN {
  @Test
  public void testBase58() {
    testEncodeDecode(BaseN.base58(), "57HPJixqLa", "light w");
    testEncodeDecode(BaseN.base58(), "K8jKTEDSMQa", "light wo");
    testEncodeDecode(BaseN.base58(), "2P37nSmKtu54d", "light wor");
    testEncodeDecode(BaseN.base58(), "76GLwaieQ4pw1k", "light work");
    testEncodeDecode(BaseN.base58(), "TtEj1upp4otZQHb", "light work.");
    testEncodeDecode(BaseN.base58(), "StV1DL6CwTryKyV", "hello world");

    testRand(BaseN.base58());
  }

  @Test
  public void testBase62() {
    testEncodeDecode(BaseN.base62(), "2Fl72NDKTP", "light w");
    testEncodeDecode(BaseN.base62(), "9J4X3nr2XR1", "light wo");
    testEncodeDecode(BaseN.base62(), "cQkiVhr0U3Zy", "light wor");
    testEncodeDecode(BaseN.base62(), "2YgStmv4s06mXT", "light work");
    testEncodeDecode(BaseN.base62(), "AZDLKLzg6yS0QCU", "light work.");
    testEncodeDecode(BaseN.base62(), "AAwf93rvy4aWQVw", "hello world");

    testRand(BaseN.base62());
  }

  private static void testEncodeDecode(final BaseN baseN, final String expected, final String input) {
    Assertions.assertEquals(expected, baseN.encode(input.getBytes(StandardCharsets.UTF_8)));
    //Assertions.assertEquals(input, new String(baseN.decode(expected.getBytes(StandardCharsets.UTF_8))));
    Assertions.assertEquals(input, new String(baseN.decode(expected)));
  }

  private static void testRand(final BaseN baseN) {
    final Random rand = new Random();
    for (int i = 0; i < 100; ++i) {
      final byte[] data = new byte[rand.nextInt(0, 1 << 11)];
      rand.nextBytes(data);
      if (data.length != 0 && data[0] == 0) {
        data[0] = 1;
      }
      final String encBytes = baseN.encode(data);
      if (BytesUtil.isFilledWithZeros(data)) {
        Assertions.assertArrayEquals(new byte[] { 0 }, baseN.decode(encBytes));
      } else {
        final byte[] dec = baseN.decode(encBytes);
        Assertions.assertArrayEquals(data, dec);
      }
      //Assertions.assertArrayEquals(data, baseN.decode(encBytes.getBytes(StandardCharsets.UTF_8)));
    }
  }
}
