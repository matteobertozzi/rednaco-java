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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public final class BytesUtil {
  public static final byte[] EMPTY_BYTES = new byte[0];
  public static final byte[] NEW_LINE = new byte[] { '\n' };
  public static final byte[] CRLF = new byte[] { '\r', '\n' };

  private BytesUtil() {
    // no-op
  }

  public interface ByteArrayConsumer {
    void accept(byte[] buf, int off, int len);
  }

  // ================================================================================
  //  Bytes Length util
  // ================================================================================
  public static int length(final byte[] data) {
    return data != null ? data.length : 0;
  }

  public static boolean isEmpty(final byte[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isNotEmpty(final byte[] input) {
    return (input != null) && (input.length != 0);
  }

  // ================================================================================
  //  Bytes Check
  // ================================================================================
  public static boolean isFilledWithZeros(final byte[] data) {
    return isFilledWith(data, 0);
  }

  public static boolean isFilledWith(final byte[] data, final int value) {
    if (isEmpty(data)) return false;

    for (int i = 0; i < data.length; ++i) {
      if ((data[i] & 0xff) != value) {
        return false;
      }
    }
    return true;
  }

  // ================================================================================
  //  Bytes to int
  // ================================================================================
  public static int parseUnsignedInt(final byte[] data, final int off, final int count)
      throws NumberFormatException {
    return Math.toIntExact(parseUnsignedLong(data, off, count));
  }

  public static long parseUnsignedLong(final byte[] data, final int off, final int count)
      throws NumberFormatException {
    long value = 0;
    for (int i = 0; i < count; ++i) {
      final int chr = data[off + i];
      if (chr >= 48 && chr <= 57) {
        value = value * 10 + (chr - 48);
      } else {
        throw new NumberFormatException();
      }
    }
    return value;
  }

  // ================================================================================
  //  Bytes equals/compare util
  // ================================================================================
  public static boolean equals(final byte[] a, final byte[] b) {
    return (isEmpty(a) && isEmpty(b)) || Arrays.equals(a, b);
  }

  public static boolean equals(final byte[] a, final int aOff, final int aLen,
      final byte[] b, final int bOff, final int bLen) {
    return Arrays.equals(a, aOff, aOff + aLen, b, bOff, bOff + bLen);
  }

  public static int compare(final byte[] a, final byte[] b) {
    final int aLen = BytesUtil.length(a);
    final int bLen = BytesUtil.length(b);
    return compare(a, 0, aLen, b, 0, bLen);
  }

  public static int compare(final byte[] a, final int aOff, final int aLen,
      final byte[] b, final int bOff, final int bLen) {
    return Arrays.compareUnsigned(a, aOff, aOff + aLen, b, bOff, bOff + bLen);
  }

  // ================================================================================
  //  Bytes concatenation
  // ================================================================================
  public static byte[] concat(final byte[]... data) {
    final byte[] fullData = new byte[length(data)];
    int offset = 0;
    for (int i = 0; i < data.length; ++i) {
      System.arraycopy(data[i], 0, fullData, offset, data[i].length);
      offset += data[i].length;
    }
    return fullData;
  }

  public static int length(final byte[]... data) {
    int length = 0;
    for (int i = 0; i < data.length; ++i) {
      length += data[i].length;
    }
    return length;
  }

  // ================================================================================
  //  String to Bytes
  // ================================================================================
  public static byte[][] toBytes(final String[] values) {
    return toBytes(values, StandardCharsets.UTF_8);
  }

  public static byte[][] toBytes(final String[] values, final Charset charsets) {
    if (ArrayUtil.isEmpty(values)) return new byte[0][];

    final byte[][] bValues = new byte[values.length][];
    for (int i = 0; i < values.length; ++i) {
      bValues[i] = values[i].getBytes(charsets);
    }
    return bValues;
  }

  // ================================================================================
  //  Bytes to binary
  // ================================================================================
  public static String toBinaryString(final byte[] buf) {
    return toBinaryString(buf, 0, length(buf));
  }

  public static String toBinaryString(final byte[] buf, final int off, final int len) {
    if (buf == null || len == 0) return "";

    final StringBuilder builder = new StringBuilder(len * 8);
    for (int i = 0; i < len; ++i) {
      if (i > 0) builder.append(' ');
      final int b = buf[off + i] & 0xff;
      for (int k = 7; k >= 0; --k) {
        builder.append((b & (1 << k)) != 0 ? '1' : '0');
      }
    }
    return builder.toString();
  }

  // ================================================================================
  //  NOTE: Use only for testing
  // ================================================================================
  public static byte[] fromInts(final int[] intBytes) {
    final byte[] bytes = new byte[intBytes.length];
    for (int i = 0; i < intBytes.length; ++i) {
      if (intBytes[i] > 0xff) throw new IllegalArgumentException("invalid byte " + intBytes[i] + " at position " + i);
      bytes[i] = (byte)intBytes[i];
    }
    return bytes;
  }

  // ================================================================================
  //  Bytes to hex
  // ================================================================================
  public static byte[] fromHexString(final String data) {
    return fromHexString(data, 0, StringUtil.length(data));
  }

  public static byte[] fromHexString(final String data, final int offset, final int length) {
    if (length == 0) return EMPTY_BYTES;

    final byte[] buffer = new byte[length >> 1];
    for (int i = 0; i < length; i += 2) {
      buffer[i >> 1] = (byte) ((Character.digit(data.charAt(offset + i), 16) << 4) + Character.digit(data.charAt(offset + i+1), 16));
    }
    return buffer;
  }

  public static byte[] toHexBytes(final byte[] buf) {
    return toHexBytes(buf, 0, length(buf));
  }

  public static byte[] toHexBytes(final byte[] buf, final int off, final int len) {
    final byte[] hex = new byte[len * 2];
    for (int i = 0, j = 0; i < len; ++i, j += 2) {
      final int val = buf[off + i] & 0xff;
      hex[j] = (byte) StringUtil.HEX_DIGITS[(val >> 4) & 0xf];
      hex[j + 1] = (byte) StringUtil.HEX_DIGITS[val & 0xf];
    }
    return hex;
  }

  public static String toHexString(final byte[] buf) {
    return toHexString(buf, 0, length(buf));
  }

  public static String toHexString(final byte[] buf, final int off, final int len) {
    return toHexString(new StringBuilder(len * 2), buf, off, len).toString();
  }

  public static StringBuilder toHexString(final StringBuilder hex, final byte[] buf, final int off, final int len) {
    if (len == 0) return hex;

    for (int i = 0; i < len; ++i) {
      final int val = buf[off + i] & 0xff;
      hex.append(StringUtil.HEX_DIGITS[(val >> 4) & 0xf]);
      hex.append(StringUtil.HEX_DIGITS[val & 0xf]);
    }
    return hex;
  }

  // ================================================================================
  //  Bytes to string
  // ================================================================================
  public static String toString(final byte[] buf) {
    return toString(buf, 0, length(buf));
  }

  public static String toString(final byte[] buf, final int off, final int len) {
    if (len == 0) return "[]";

    final StringBuilder builder = new StringBuilder(len * 4);
    builder.append('[');
    builder.append(buf[off] & 0xff);
    for (int i = 1; i < len; ++i) {
      builder.append(", ");
      builder.append(buf[off + i] & 0xff);
    }
    builder.append(']');
    return builder.toString();
  }
}
