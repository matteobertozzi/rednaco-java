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

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;

public class Base16 {
  private static final class HolderBase16 {
    private static final Base16 BASE16 = new Base16("0123456789abcdefABCDEF");
  }

  public static Base16 base16() { return HolderBase16.BASE16; }

  private final char[] alphabet;
  private final short[] decodeTable;

  public Base16(final String alphabet) {
    this(alphabet.toCharArray());
  }

  public Base16(final char[] alphabet) {
    this(alphabet, BaseN.buildDecodeTable(alphabet));
  }

  public Base16(final char[] alphabet, final short[] decodeTable) {
    this.alphabet = alphabet;
    this.decodeTable = decodeTable;
  }

  // ====================================================================================================
  //  Encode related
  // ====================================================================================================
  public String encode(final byte[] data) {
    return encode(data, 0, BytesUtil.length(data));
  }

  public String encode(final byte[] data, final int offset, final int length) {
    if (length == 0) return "";

    final StringBuilder builder = new StringBuilder(length * 2);
    encode(builder, data, offset, length);
    return builder.toString();
  }

  public void encode(final StringBuilder builder, final byte[] data, final int offset, final int length) {
    for (int i = 0; i < length; ++i) {
      encodeByte(builder, data[offset + i]);
    }
  }

  public void encodeByte(final StringBuilder builder, final int value) {
    final int val = value & 0xff;
    builder.append(alphabet[(val >> 4) & 0xf]);
    builder.append(alphabet[val & 0xf]);
  }

  public String encodeInt32(final int i32) {
    final StringBuilder builder = new StringBuilder(8);
    encodeInt32(builder, i32);
    return builder.toString();
  }

  public void encodeInt32(final StringBuilder builder, final int i32) {
    encodeByte(builder, (i32 >>> 24) & 0xff);
    encodeByte(builder, (i32 >>> 16) & 0xff);
    encodeByte(builder, (i32 >>> 8) & 0xff);
    encodeByte(builder, i32 & 0xff);
  }

  public String encodeInt64(final long i64) {
    final StringBuilder builder = new StringBuilder(16);
    encodeInt64(builder, i64);
    return builder.toString();
  }

  public void encodeInt64(final StringBuilder builder, final long i64) {
    encodeByte(builder, (int)((i64 >>> 56) & 0xff));
    encodeByte(builder, (int)((i64 >>> 48) & 0xff));
    encodeByte(builder, (int)((i64 >>> 40) & 0xff));
    encodeByte(builder, (int)((i64 >>> 32) & 0xff));
    encodeByte(builder, (int)((i64 >>> 24) & 0xff));
    encodeByte(builder, (int)((i64 >>> 16) & 0xff));
    encodeByte(builder, (int)((i64 >>> 8) & 0xff));
    encodeByte(builder, (int)(i64 & 0xff));
  }

  // ====================================================================================================
  //  Decode byte[] related
  // ====================================================================================================
  public byte[] decode(final byte[] encoded) {
    return decode(encoded, 0, BytesUtil.length(encoded));
  }

  public byte[] decode(final byte[] encoded, final int offset, final int length) {
    if (length == 0) return null;

    final byte[] buffer = new byte[length >> 1];
    for (int i = 0, off = 0; i < length; ++off, i += 2) {
      buffer[off] = (byte) ((decodeTable[encoded[offset + i]] << 4) | decodeTable[encoded[offset + i + 1]]);
    }
    return buffer;
  }

  // ====================================================================================================
  //  Decode String related
  // ====================================================================================================
  public byte[] decode(final String encoded) {
    return decode(encoded, 0, StringUtil.length(encoded));
  }

  public byte[] decode(final String encoded, final int offset, final int length) {
    if (StringUtil.isEmpty(encoded)) return null;

    final byte[] buffer = new byte[length >> 1];
    for (int i = 0, off = 0; i < length; ++off, i += 2) {
      buffer[off] = (byte) ((decodeTable[encoded.charAt(offset + i)] << 4) | decodeTable[encoded.charAt(offset + i + 1)]);
    }
    return buffer;
  }

  public int decodeInt32(final String encoded) {
    return decodeInt32(encoded, 0, StringUtil.length(encoded));
  }

  public int decodeInt32(final String encoded, final int offset, final int length) {
    int i32 = 0;
    int shift = (length - 1) << 2;
    for (int i = 0; i < length; ++i) {
      i32 |= decodeTable[encoded.charAt(offset + i)] << shift;
      shift -= 4;
    }
    return i32;
  }

  public int decodeInt32(final byte[] encoded) {
    return decodeInt32(encoded, 0, BytesUtil.length(encoded));
  }

  public int decodeInt32(final byte[] encoded, final int offset, final int length) {
    int i32 = 0;
    int shift = (length - 1) << 2;
    for (int i = 0; i < length; ++i) {
      i32 |= decodeTable[encoded[offset + i]] << shift;
      shift -= 4;
    }
    return i32;
  }

  public long decodeInt64(final String encoded) {
    return decodeInt64(encoded, 0, StringUtil.length(encoded));
  }

  public long decodeInt64(final String encoded, final int offset, final int length) {
    long i64 = 0;
    int shift = (length - 1) << 2;
    for (int i = 0; i < length; ++i) {
      i64 |= ((long)decodeTable[encoded.charAt(offset + i)]) << shift;
      shift -= 4;
    }
    return i64;
  }

  public long decodeInt64(final byte[] encoded) {
    return decodeInt64(encoded, 0, BytesUtil.length(encoded));
  }

  public long decodeInt64(final byte[] encoded, final int offset, final int length) {
    long i64 = 0;
    int shift = (length - 1) << 2;
    for (int i = 0; i < length; ++i) {
      i64 |= ((long)decodeTable[encoded[offset + i]]) << shift;
      shift -= 4;
    }
    return i64;
  }
}
