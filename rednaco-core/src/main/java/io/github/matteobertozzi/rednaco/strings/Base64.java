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

//  Base64
// ------------------------------------------------+
//           1           2           3           4 | base64 chars
// 0 1 2 3 4 5 0 1 2 3 4 5 0 1 2 3 4 5 0 1 2 3 4 5 |
// 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 |
//               1               2               3 | bytes
public class Base64 {
  private static final class HolderBase64 {
    private static final Base64 BASE64 = new Base64("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
  }
  private static final class HolderBase64Hex {
    private static final Base64 BASE64_HEX = new Base64("-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz");
  }

  public static Base64 base64() { return HolderBase64.BASE64; }
  public static Base64 base64hex() { return HolderBase64Hex.BASE64_HEX; }

  private final char[] alphabet;
  private final short[] decodeTable;

  public Base64(final String alphabet) {
    this(alphabet.toCharArray());
  }

  public Base64(final char[] alphabet) {
    this(alphabet, BaseN.buildDecodeTable(alphabet));
  }

  public Base64(final char[] alphabet, final short[] decodeTable) {
    this.alphabet = alphabet;
    this.decodeTable = decodeTable;
  }

  // ====================================================================================================
  //  Encode related
  // ====================================================================================================
  public String encode(final byte[] data) {
    return encode(data, 0, BytesUtil.length(data));
  }

  public String encode(final byte[] data, final int off, final int length) {
    final StringBuilder builder = new StringBuilder(length * 2);
    encode(builder, data, off, length);
    return builder.toString();
  }

  public void encode(final StringBuilder builder, final byte[] data, final int off, final int length) {
    int offset = off;
    while ((offset + 3) <= length) {
      final int v0 = data[offset++] & 0xff;
      final int v1 = data[offset++] & 0xff;
      final int v2 = data[offset++] & 0xff;

      builder.append(alphabet[v0 >>> 2]);
      builder.append(alphabet[((v0 & 3) << 4) | (v1 >>> 4)]);
      builder.append(alphabet[((v1 & 0xf) << 2) | (v2 >>> 6)]);
      builder.append(alphabet[v2 & 0x3f]);
    }

    switch (length - offset) {
      case 1: {
        final int v0 = data[offset] & 0xff;
        builder.append(alphabet[v0 >>> 2]);
        builder.append(alphabet[(v0 & 3) << 4]);
        break;
      }
      case 2: {
        final int v0 = data[offset++] & 0xff;
        final int v1 = data[offset] & 0xff;
        builder.append(alphabet[v0 >>> 2]);
        builder.append(alphabet[((v0 & 3) << 4) | (v1 >>> 4)]);
        builder.append(alphabet[(v1 & 0xf) << 2]);
        break;
      }
    }
  }

  // ====================================================================================================
  //  Decode byte[] related
  // ====================================================================================================
  public byte[] decode(final byte[] encoded) {
    return decode(encoded, 0, BytesUtil.length(encoded));
  }

  public byte[] decode(final byte[] encoded, final int offset, final int length) {
    if (length == 0) return null;

    final byte[] buffer = new byte[lengthDecoded(length)];
    int i = offset;
    int index = 0;
    while ((i + 4) <= length) {
      final int v0 = decodeTable[encoded[i++] & 0xff];
      final int v1 = decodeTable[encoded[i++] & 0xff];
      final int v2 = decodeTable[encoded[i++] & 0xff];
      final int v3 = decodeTable[encoded[i++] & 0xff];
      if ((v0 | v1 | v2 | v3) < 0) {
        throw new IllegalArgumentException("invalid encoded data: " + BytesUtil.toString(encoded, offset, length));
      }
      buffer[index++] = (byte)((v0 << 2) | v1 >> 4);
      buffer[index++] = (byte)((v1 << 4) | v2 >> 2);
      buffer[index++] = (byte)((v2 << 6) | v3);
    }

    switch (length - i) {
      case 2: {
        final int v0 = decodeTable[encoded[i++] & 0xff];
        final int v1 = decodeTable[encoded[i] & 0xff];
        if ((v0 | v1) < 0) {
          throw new IllegalArgumentException("invalid encoded data: " + BytesUtil.toString(encoded, offset, length));
        }
        buffer[index] = (byte)((v0 << 2) | v1 >> 4);
        break;
      }
      case 3: {
        final int v0 = decodeTable[encoded[i++] & 0xff];
        final int v1 = decodeTable[encoded[i++] & 0xff];
        final int v2 = decodeTable[encoded[i] & 0xff];
        if ((v0 | v1 | v2) < 0) {
          throw new IllegalArgumentException("invalid encoded data: " + BytesUtil.toString(encoded, offset, length));
        }
        buffer[index++] = (byte)((v0 << 2) | v1 >> 4);
        buffer[index] = (byte)((v1 << 4) | v2 >> 2);
        break;
      }
    }
    return buffer;
  }

  // ====================================================================================================
  //  Decode String related
  // ====================================================================================================
  public byte[] decode(final String data) {
    return decode(data, 0, StringUtil.length(data));
  }

  public byte[] decode(final String data, final int offset, final int length) {
    if (length == 0) return BytesUtil.EMPTY_BYTES;

    final byte[] buffer = new byte[lengthDecoded(length)];
    int i = offset;
    int index = 0;
    while ((i + 4) <= length) {
      final int v0 = decodeTable[data.charAt(i++)];
      final int v1 = decodeTable[data.charAt(i++)];
      final int v2 = decodeTable[data.charAt(i++)];
      final int v3 = decodeTable[data.charAt(i++)];
      if ((v0 | v1 | v2 | v3) < 0) {
        throw new IllegalArgumentException("invalid encoded data: " + data);
      }
      buffer[index++] = (byte)((v0 << 2) | v1 >> 4);
      buffer[index++] = (byte)((v1 << 4) | v2 >> 2);
      buffer[index++] = (byte)((v2 << 6) | v3);
    }

    switch (length - i) {
      case 2: {
        final int v0 = decodeTable[data.charAt(i++)];
        final int v1 = decodeTable[data.charAt(i)];
        if ((v0 | v1) < 0) {
          throw new IllegalArgumentException("invalid encoded data: " + data);
        }
        buffer[index] = (byte)((v0 << 2) | v1 >> 4);
        break;
      }
      case 3: {
        final int v0 = decodeTable[data.charAt(i++)];
        final int v1 = decodeTable[data.charAt(i++)];
        final int v2 = decodeTable[data.charAt(i)];
        if ((v0 | v1 | v2) < 0) {
          throw new IllegalArgumentException("invalid encoded data: " + data);
        }
        buffer[index++] = (byte)((v0 << 2) | v1 >> 4);
        buffer[index] = (byte)((v1 << 4) | v2 >> 2);
        break;
      }
    }
    return buffer;
  }

  private static int lengthDecoded(final int encodedLength) {
    final int length = (encodedLength >>> 2) * 3;
    return switch (encodedLength & 3) {
      case 2 -> length + 1;
      case 3 -> length + 2;
      default -> length;
    };
  }
}
