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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;
import io.github.matteobertozzi.rednaco.bytes.PagedByteArray;
import io.github.matteobertozzi.rednaco.util.RandData;

public class TestIntEncoding {
  @Test
  public void testIntBeEncoding() {
    final byte[] buf = new byte[8];

    IntEncoder.BIG_ENDIAN.writeFixed8(buf, 3, 0);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed8(buf, 3, 0x7b);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x7b, 0, 0, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed8(buf, 3, 0xff);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xff, 0, 0, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed16(buf, 3, 0x0B0D);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x0B, 0x0D, 0, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed16(buf, 3, 0xABCD);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xAB, (byte)0xCD, 0, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed24(buf, 3, 0x0B0D0E);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x0B, 0x0D, 0x0E, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed24(buf, 3, 0xABCDEF);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xAB, (byte)0xCD, (byte)0xEF, 0, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed32(buf, 3, 0x0B0D0E05);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x0B, 0x0D, 0x0E, 0x05, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed32(buf, 3, 0xABCDEF97L);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x97, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed32(buf, 3, 0xffffffffL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed40(buf, 2, 0xABCDEF971BL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x97, (byte)0x1B, 0 }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed48(buf, 2, 0xABCDEF971B3DL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x97, (byte)0x1B, (byte)0x3D }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed56(buf, 1, 0xABCDEF971B3D5FL);
    Assertions.assertArrayEquals(new byte[] { 0, (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x97, (byte)0x1B, (byte)0x3D, (byte)0x5F }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed64(buf, 0, 0xABCDEF971B3D5F7EL);
    Assertions.assertArrayEquals(new byte[] { (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x97, (byte)0x1B, (byte)0x3D, (byte)0x5F, (byte)0x7E }, buf);

    IntEncoder.BIG_ENDIAN.writeFixed64(buf, 0, 0xffffffffffffffffL);
    Assertions.assertArrayEquals(new byte[] { (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }, buf);
  }

  @Test
  public void testIntLeEncoding() {
    final byte[] buf = new byte[8];

    IntEncoder.LITTLE_ENDIAN.writeFixed8(buf, 3, 0);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed8(buf, 3, 0x7b);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x7b, 0, 0, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed8(buf, 3, 0xff);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xff, 0, 0, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed16(buf, 3, 0x0B0D);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x0D, 0x0B, 0, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed16(buf, 3, 0xABCD);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xCD, (byte)0xAB, 0, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed24(buf, 3, 0x0B0D0E);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x0E, 0x0D, 0x0B, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed24(buf, 3, 0xABCDEF);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xEF, (byte)0xCD, (byte)0xAB, 0, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed32(buf, 3, 0x0B0D0E05);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0x05, 0x0E, 0x0D, 0x0B, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed32(buf, 3, 0xABCDEF97L);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0x97, (byte)0xEF, (byte)0xCD, (byte)0xAB, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed32(buf, 3, 0xffffffffL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed40(buf, 2, 0xABCDEF971BL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, (byte)0x1B, (byte)0x97, (byte)0xEF, (byte)0xCD, (byte)0xAB, 0 }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed48(buf, 2, 0xABCDEF971B3DL);
    Assertions.assertArrayEquals(new byte[] { 0, 0, (byte)0x3D, (byte)0x1B, (byte)0x97, (byte)0xEF, (byte)0xCD, (byte)0xAB }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed56(buf, 1, 0xABCDEF971B3D5FL);
    Assertions.assertArrayEquals(new byte[] { 0, (byte)0x5F, (byte)0x3D, (byte)0x1B, (byte)0x97, (byte)0xEF, (byte)0xCD, (byte)0xAB }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed64(buf, 0, 0xABCDEF971B3D5F7EL);
    Assertions.assertArrayEquals(new byte[] { (byte)0x7E, (byte)0x5F, (byte)0x3D, (byte)0x1B, (byte)0x97, (byte)0xEF, (byte)0xCD, (byte)0xAB }, buf);

    IntEncoder.LITTLE_ENDIAN.writeFixed64(buf, 0, 0xffffffffffffffffL);
    Assertions.assertArrayEquals(new byte[] { (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }, buf);
  }

  @Test
  public void testRandIntBeEncoding() {
    final byte[] buf = new byte[16];
    long lastMax = 0;
    for (int i = 1; i < 8; ++i) {
      final long currentMax = (1L << (i * 8)) - 1;
      final long v = RandData.generateLong(lastMax, currentMax);
      final int width = IntUtil.size(v);
      Assertions.assertEquals(i, width);

      Arrays.fill(buf, (byte)0xff);
      IntEncoder.BIG_ENDIAN.writeFixed(buf, 3, v, width);
      Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed(buf, 3, width));

      Arrays.fill(buf, (byte)0xff);
      IntEncoder.LITTLE_ENDIAN.writeFixed(buf, 3, v, width);
      Assertions.assertEquals(v, IntDecoder.LITTLE_ENDIAN.readFixed(buf, 3, width));

      lastMax = currentMax;
    }

    final long v = RandData.generateLong(lastMax, 0x7fffffffffffffffL);
    final int width = IntUtil.size(v);
    Assertions.assertEquals(8, width);

    Arrays.fill(buf, (byte)0xff);
    IntEncoder.BIG_ENDIAN.writeFixed(buf, 3, v, width);
    Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed(buf, 3, width));

    Arrays.fill(buf, (byte)0xff);
    IntEncoder.LITTLE_ENDIAN.writeFixed(buf, 3, v, width);
    Assertions.assertEquals(v, IntDecoder.LITTLE_ENDIAN.readFixed(buf, 3, width));
  }

  @Test
  public void testIntEncodingBuffer() {
    final int[] blocks = new int[] { -1, 0xff, 0xffff, 0xffffff, 0x7fffffff };

    final byte[] buf = new byte[8];
    for (int off = 0; off < 4; ++off) {
      for (int i = 1; i < blocks.length; ++i) {
        for (int k = 0; k < 100; ++k) {
          final int v = RandData.generateInt(blocks[i - 1] + 1, blocks[i]);
          final int width = IntUtil.size(v);
          Arrays.fill(buf, (byte)0xff);
          IntEncoder.BIG_ENDIAN.writeFixed(buf, off, v, width);
          Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed(buf, off, width));

          Arrays.fill(buf, (byte)0xff);
          IntEncoder.LITTLE_ENDIAN.writeFixed(buf, off, v, width);
          Assertions.assertEquals(v, IntDecoder.LITTLE_ENDIAN.readFixed(buf, off, width));
        }
      }

      for (int k = 0; k < 100; ++k) {
        int v = RandData.generateInt(0, 0xff);
        Arrays.fill(buf, (byte)0xff);
        IntEncoder.BIG_ENDIAN.writeFixed8(buf, off, v);
        Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed8(buf, off));

        v = RandData.generateInt(0x100, 0xffff);
        Arrays.fill(buf, (byte)0xff);
        IntEncoder.BIG_ENDIAN.writeFixed16(buf, off, v);
        Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed16(buf, off));

        v = RandData.generateInt(0x10000, 0xffffff);
        Arrays.fill(buf, (byte)0xff);
        IntEncoder.BIG_ENDIAN.writeFixed24(buf, off, v);
        Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed24(buf, off));

        v = RandData.generateInt(0x1000000, 0x7fffffff);
        Arrays.fill(buf, (byte)0xff);
        IntEncoder.BIG_ENDIAN.writeFixed32(buf, off, v);
        Assertions.assertEquals(v, IntDecoder.BIG_ENDIAN.readFixed32(buf, off));
      }
    }
  }

  @Test
  public void testVarIntEncode() {
    final byte[] buf = new byte[9];
    int len;

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 0);
    Assertions.assertEquals(1, len);

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 1);
    Assertions.assertEquals(1, len);
    Assertions.assertArrayEquals(new byte[] { 1 }, Arrays.copyOf(buf, len));

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 127);
    Assertions.assertEquals(1, len);
    Assertions.assertArrayEquals(new byte[] { 127 }, Arrays.copyOf(buf, len));

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 128);
    Assertions.assertEquals(2, len);
    Assertions.assertArrayEquals(new byte[] { (byte)128, 1 }, Arrays.copyOf(buf, len));

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 0xff);
    Assertions.assertEquals(2, len);
    Assertions.assertArrayEquals(new byte[] { (byte)255, (byte)1 }, Arrays.copyOf(buf, len));

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 0xffff);
    Assertions.assertEquals(3, len);
    Assertions.assertArrayEquals(new byte[] { (byte)255, (byte)255, (byte)3 }, Arrays.copyOf(buf, len));

    len = IntEncoder.writeUnsignedVarLong(buf, 0, 0xabcdef1234L);
    Assertions.assertEquals(6, len);
    Assertions.assertArrayEquals(new byte[] { (byte)0xb4, (byte)0xa4, (byte)0xbc, (byte)0xef, (byte)0xbc, 0x15 }, Arrays.copyOf(buf, len));
  }

  public static void main(final String[] args) {
    System.out.println(BytesUtil.toHexString(new byte[] { -76, -92, -68, -17, -68, 21 }));
  }

  @Test
  public void testRandVarIntEncodeDecode() throws IOException {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
      final PagedByteArray pagedBuf = new PagedByteArray();
      final byte[] buf = new byte[9];
      for (int k = 0; k < 10000; ++k) {
        final long value = RandData.generateLong(0, Long.MAX_VALUE);
        final int length = IntUtil.unsignedVarLongSize(value);

        final int bufLen = IntEncoder.writeUnsignedVarLong(buf, 0, value);
        Assertions.assertEquals(length, bufLen);
        final byte[] rbuf = Arrays.copyOf(buf, length);

        final int pagedBufLen = IntEncoder.writeUnsignedVarLong(pagedBuf, value);
        Assertions.assertEquals(length, pagedBufLen);
        Assertions.assertArrayEquals(rbuf, pagedBuf.toByteArray());

        final int streamLen = IntEncoder.writeUnsignedVarLong(stream, value);
        Assertions.assertEquals(length, streamLen);
        Assertions.assertArrayEquals(rbuf, stream.toByteArray());

        Assertions.assertEquals(value, IntDecoder.readUnsignedVarLong(buf, 0));
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
          Assertions.assertEquals(value, IntDecoder.readUnsignedVarLong(buf, 0));
        }

        Arrays.fill(buf, (byte)0);
        pagedBuf.clear();
        stream.reset();
      }
    }
  }
}
