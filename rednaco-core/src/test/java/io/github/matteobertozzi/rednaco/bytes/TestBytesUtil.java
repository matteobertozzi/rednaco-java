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

package io.github.matteobertozzi.rednaco.bytes;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBytesUtil {

  @Test
  public void testSize() {
    Assertions.assertEquals(0, BytesUtil.length((byte[])null));
    Assertions.assertEquals(0, BytesUtil.length(new byte[0]));
    Assertions.assertEquals(1, BytesUtil.length(new byte[] { 10 }));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(BytesUtil.isEmpty(null));
    Assertions.assertTrue(BytesUtil.isEmpty(new byte[0]));
    Assertions.assertFalse(BytesUtil.isEmpty(new byte[] { 10 }));

    Assertions.assertFalse(BytesUtil.isNotEmpty(null));
    Assertions.assertFalse(BytesUtil.isNotEmpty(new byte[0]));
    Assertions.assertTrue(BytesUtil.isNotEmpty(new byte[] { 10 }));
  }

  @Test
  public void testIsFilledWithZeros() {
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(null));
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(new byte[0]));
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(new byte[] { 0, 1, 0, 2 }));
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(new byte[] { 0, 0, 0, 1 }));
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(new byte[] { 1, 0, 0, 0 }));
    Assertions.assertFalse(BytesUtil.isFilledWithZeros(new byte[] { 0, 0, 1, 0 }));

    Assertions.assertTrue(BytesUtil.isFilledWithZeros(new byte[] { 0 }));
    Assertions.assertTrue(BytesUtil.isFilledWithZeros(new byte[] { 0, 0 }));
    Assertions.assertTrue(BytesUtil.isFilledWithZeros(new byte[] { 0, 0, 0 }));
    Assertions.assertTrue(BytesUtil.isFilledWithZeros(new byte[] { 0, 0, 0, 0 }));
  }

  @Test
  public void testIsFilledWith() {
    Assertions.assertFalse(BytesUtil.isFilledWith(null, 5));
    Assertions.assertFalse(BytesUtil.isFilledWith(new byte[0], 5));
    Assertions.assertFalse(BytesUtil.isFilledWith(new byte[] { 0, 5, 0, 2 }, 5));
    Assertions.assertFalse(BytesUtil.isFilledWith(new byte[] { 0, 0, 0, 5 }, 5));
    Assertions.assertFalse(BytesUtil.isFilledWith(new byte[] { 5, 0, 0, 0 }, 5));
    Assertions.assertFalse(BytesUtil.isFilledWith(new byte[] { 0, 0, 5, 0 }, 5));

    Assertions.assertTrue(BytesUtil.isFilledWith(new byte[] { 5 }, 5));
    Assertions.assertTrue(BytesUtil.isFilledWith(new byte[] { 5, 5 }, 5));
    Assertions.assertTrue(BytesUtil.isFilledWith(new byte[] { 5, 5, 5 }, 5));
    Assertions.assertTrue(BytesUtil.isFilledWith(new byte[] { 5, 5, 5, 5 }, 5));
  }

  @Test
  public void testParseUnsignedInt() {
    Assertions.assertEquals(1, BytesUtil.parseUnsignedLong("1".getBytes(StandardCharsets.UTF_8), 0, 1));
    Assertions.assertEquals(64, BytesUtil.parseUnsignedLong("64".getBytes(StandardCharsets.UTF_8), 0, 2));
    Assertions.assertEquals(255, BytesUtil.parseUnsignedLong("255".getBytes(StandardCharsets.UTF_8), 0, 3));
  }

  @Test
  public void testFromInt() {
    Assertions.assertArrayEquals(new byte[] { 0, 1, 2, 126, (byte)128, (byte)180, (byte)200, (byte)255 },
                                 BytesUtil.fromInts(new int[] { 0, 1, 2, 126, 128, 180, 200, 255 }));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> BytesUtil.fromInts(new int[] { 1, 2, 300, 4 }));
  }

  @Test
  public void testFromHex() {
    Assertions.assertArrayEquals(new byte[0], BytesUtil.fromHexString(null));
    Assertions.assertArrayEquals(new byte[0], BytesUtil.fromHexString(""));
    Assertions.assertArrayEquals(new byte[] { 1 }, BytesUtil.fromHexString("01"));
    Assertions.assertArrayEquals(new byte[] { 1, 15 }, BytesUtil.fromHexString("010f"));
    Assertions.assertArrayEquals(new byte[] { 1, 15, 32 }, BytesUtil.fromHexString("010f20"));
    Assertions.assertArrayEquals(new byte[] { 1, 15, 32, (byte)128, (byte)255 }, BytesUtil.fromHexString("010f2080ff"));
  }

  @Test
  public void testToHex() {
    Assertions.assertEquals("", BytesUtil.toHexString(null));
    Assertions.assertEquals("", BytesUtil.toHexString(new byte[0]));
    Assertions.assertEquals("01", BytesUtil.toHexString(new byte[] { 1 }));
    Assertions.assertEquals("010f", BytesUtil.toHexString(new byte[] { 1, 15 }));
    Assertions.assertEquals("010f20", BytesUtil.toHexString(new byte[] { 1, 15, 32 }));
    Assertions.assertEquals("010f2080ff", BytesUtil.toHexString(new byte[] { 1, 15, 32, (byte)128, (byte)255 }));
  }

  @Test
  public void testToBinaryString() {
    Assertions.assertEquals("", BytesUtil.toBinaryString(null));
    Assertions.assertEquals("", BytesUtil.toBinaryString(new byte[0]));
    Assertions.assertEquals("00000001 00000010 00000011 00000100 11111111", BytesUtil.toBinaryString(new byte[] { 1, 2, 3, 4, (byte)0xff }));

    Assertions.assertEquals("00000010 00000011 00000100", BytesUtil.toBinaryString(new byte[] { 1, 2, 3, 4, (byte)0xff }, 1, 3));
    Assertions.assertEquals("00000011 00000100 11111111", BytesUtil.toBinaryString(new byte[] { 1, 2, 3, 4, (byte)0xff }, 2, 3));
  }

  @Test
  public void testToString() {
    Assertions.assertEquals("[]", BytesUtil.toString(null));
    Assertions.assertEquals("[]", BytesUtil.toString(new byte[0]));
    Assertions.assertEquals("[1, 2, 3, 4, 255]", BytesUtil.toString(new byte[] { 1, 2, 3, 4, (byte)0xff }));
    Assertions.assertEquals("[2, 3, 4]", BytesUtil.toString(new byte[] { 1, 2, 3, 4, (byte)0xff }, 1, 3));
    Assertions.assertEquals("[3, 4, 255]", BytesUtil.toString(new byte[] { 1, 2, 3, 4, (byte)0xff }, 2, 3));
  }

  @Test
  public void testEquals() {
    Assertions.assertTrue(BytesUtil.equals(new byte[0], new byte[0]));
    Assertions.assertTrue(BytesUtil.equals(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 }));
    Assertions.assertTrue(BytesUtil.equals(new byte[] { (byte)0xff, (byte)0x80 }, new byte[] { (byte)0xff, (byte)0x80 }));
    Assertions.assertFalse(BytesUtil.equals(new byte[] { 1, 2, 3 }, new byte[] { 2, 1, 3 }));
    Assertions.assertFalse(BytesUtil.equals(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3, 4 }));
    Assertions.assertFalse(BytesUtil.equals(new byte[] { 1, 2, 3, 4 }, new byte[] { 1, 2, 3 }));
  }

  @Test
  public void testCompare() {
    Assertions.assertEquals(0, BytesUtil.compare(new byte[0], new byte[0]));
    Assertions.assertEquals(0, BytesUtil.compare(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 }));
    Assertions.assertEquals(0, BytesUtil.compare(new byte[] { (byte)0xff, (byte)0x80 }, new byte[] { (byte)0xff, (byte)0x80 }));
    Assertions.assertEquals(-1, BytesUtil.compare(new byte[] { 1, 2, 3 }, new byte[] { 2, 1, 3 }));
    Assertions.assertEquals(-1, BytesUtil.compare(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3, 4 }));
    Assertions.assertEquals(1, BytesUtil.compare(new byte[] { 1, 2, 3, 4 }, new byte[] { 1, 2, 3 }));
  }
}
