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

//  Base32
// --------------------------------------------------------------------------------+
//         1         2         3         4         5         6         7         8 | base32 chars
// 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 |
// 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 |
//               1               2               3               4               5 | bytes
public class Base32 {
  private static final class HolderBase32 {
    private static final Base32 BASE32 = new Base32("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
  }
  private static final class HolderBase32Hex {
    private static final Base32 BASE32_HEX = new Base32("0123456789ABCDEFGHIJKLMNOPQRSTUV");
  }
  private static final class HolderBase32Crockford {
    private static final Base32 BASE32_CROCKFORD = new Base32("0123456789ABCDEFGHJKMNPQRSTVWXYZ");
  }
  private static final class HolderZBase32 {
    private static final Base32 Z_BASE32 = new Base32("ybndrfg8ejkmcpqxot1uwisza345h769");
  }

  public static Base32 base32() { return HolderBase32.BASE32; }
  public static Base32 base32hex() { return HolderBase32Hex.BASE32_HEX; }
  public static Base32 crockfordBase32() { return HolderBase32Crockford.BASE32_CROCKFORD; }
  public static Base32 zbase32() { return HolderZBase32.Z_BASE32; }

  private final char[] alphabet;
  private final short[] decodeTable;

  public Base32(final String alphabet) {
    this(alphabet.toCharArray());
  }

  public Base32(final char[] alphabet) {
    this(alphabet, BaseN.buildDecodeTable(alphabet));
  }

  public Base32(final char[] alphabet, final short[] decodeTable) {
    this.alphabet = alphabet;
    this.decodeTable = decodeTable;
  }

  // ====================================================================================================
  //  Encode related
  // ====================================================================================================
  private static int lengthEncoded(final int decodedLength) {
    final int length = (decodedLength / 5) << 3;
    return switch (decodedLength % 5) {
      case 4 -> length + 7;
      case 3 -> length + 5;
      case 2 -> length + 4;
      case 1 -> length + 2;
      default -> length;
    };
  }

  public String encode(final byte[] data) {
    return encode(data, 0, BytesUtil.length(data));
  }

  public String encode(final byte[] data, final int offset, final int length) {
    if (length == 0) return "";

    final StringBuilder builder = new StringBuilder(lengthEncoded(length));
    encode(builder, data, offset, length);
    return builder.toString();
  }

  public void encode(final StringBuilder builder, final byte[] data, final int offset, int length) {
    int i = offset;
    while (length >= 5) {
      final long x = ((long)(data[i] & 0xff) << 32)
                    | ((long)(data[i + 1] & 0xff) << 24)
                    | (data[i + 2] & 0xff) << 16
                    | (data[i + 3] & 0xff) << 8
                    | (data[i + 4] & 0xff);
      builder.append(alphabet[(int)((x >> 35) & 0x1f)]);
      builder.append(alphabet[(int)((x >> 30) & 0x1f)]);
      builder.append(alphabet[(int)((x >> 25) & 0x1f)]);
      builder.append(alphabet[(int)((x >> 20) & 0x1f)]);
      builder.append(alphabet[(int)((x >> 15) & 0x1f)]);
      builder.append(alphabet[(int)((x >> 10) & 0x1f)]);
      builder.append(alphabet[(int)((x >>  5) & 0x1f)]);
      builder.append(alphabet[(int)((x) & 0x1f)]);
      length -= 5;
      i += 5;
    }

    switch (length) {
      case 4: // 7byte
        final long x4 = ((long)(data[i] & 0xff) << 27)
                      | (data[i + 1] & 0xff) << 19
                      | (data[i + 2] & 0xff) << 11
                      | (data[i + 3] & 0xff) << 3;
        builder.append(alphabet[(int)((x4 >> 30) & 0x1f)]);
        builder.append(alphabet[(int)((x4 >> 25) & 0x1f)]);
        builder.append(alphabet[(int)((x4 >> 20) & 0x1f)]);
        builder.append(alphabet[(int)((x4 >> 15) & 0x1f)]);
        builder.append(alphabet[(int)((x4 >> 10) & 0x1f)]);
        builder.append(alphabet[(int)((x4 >>  5) & 0x1f)]);
        builder.append(alphabet[(int)((x4) & 0x1f)]);
        break;
      case 3: // 5byte
        final int x3 =  (data[i] & 0xff) << 17 |
                        (data[i + 1] & 0xff) << 9  |
                        (data[i + 2] & 0xff) << 1;
        builder.append(alphabet[(x3 >> 20) & 0x1f]);
        builder.append(alphabet[(x3 >> 15) & 0x1f]);
        builder.append(alphabet[(x3 >> 10) & 0x1f]);
        builder.append(alphabet[(x3 >>  5) & 0x1f]);
        builder.append(alphabet[(x3) & 0x1f]);
        break;
      case 2: // 4byte
        final int x2 = ((data[i] & 0xff) << 12) | ((data[i + 1] & 0xff) << 4);
        builder.append(alphabet[(x2 >> 15) & 0x1f]);
        builder.append(alphabet[(x2 >> 10) & 0x1f]);
        builder.append(alphabet[(x2 >>  5) & 0x1f]);
        builder.append(alphabet[(x2) & 0x1f]);
        break;
      case 1: // 2byte
        final int x1 = (data[i] & 0xff) << 2;
        builder.append(alphabet[(x1 >>  5) & 0x1f]);
        builder.append(alphabet[(x1) & 0x1f]);
        break;
    }
  }

  // ====================================================================================================
  //  Decode byte[] related
  // ====================================================================================================
  public byte[] decode(final byte[] encoded) {
    return decode(encoded, 0, BytesUtil.length(encoded));
  }

  public byte[] decode(final byte[] encoded, final int offset, int length) {
    if (length == 0) return null;

    final byte[] data = new byte[lengthDecoded(length)];
    int dataIndex = 0;

    int i = offset;
    while (length >= 8) {
      final long x5 = ((long)(decodeTable[encoded[i] & 0xff]) << 35)
                    | ((long)(decodeTable[encoded[i + 1] & 0xff]) << 30)
                    | ((long)(decodeTable[encoded[i + 2] & 0xff]) << 25)
                    | ((long)(decodeTable[encoded[i + 3] & 0xff]) << 20)
                    | ((long)(decodeTable[encoded[i + 4] & 0xff]) << 15)
                    | ((long)(decodeTable[encoded[i + 5] & 0xff]) << 10)
                    | ((long)(decodeTable[encoded[i + 6] & 0xff]) <<  5)
                    | ((long)(decodeTable[encoded[i + 7] & 0xff]));
          data[dataIndex++] = (byte)((x5 >> 32) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 24) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 16) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 8) & 0xff);
          data[dataIndex++] = (byte)((x5) & 0xff);

      i += 8;
      length -= 8;
    }

    switch (length) {
      case 7:
        final long x4 = ((long)(decodeTable[encoded[i] & 0xff]) << 30)
                      | ((long)(decodeTable[encoded[i + 1] & 0xff]) << 25)
                      | ((long)(decodeTable[encoded[i + 2] & 0xff]) << 20)
                      | ((long)(decodeTable[encoded[i + 3] & 0xff]) << 15)
                      | ((long)(decodeTable[encoded[i + 4] & 0xff]) << 10)
                      | ((long)(decodeTable[encoded[i + 5] & 0xff]) <<  5)
                      | ((long)(decodeTable[encoded[i + 6] & 0xff]));
        data[dataIndex++] = (byte)((x4 >> 27) & 0xff);
        data[dataIndex++] = (byte)((x4 >> 19) & 0xff);
        data[dataIndex++] = (byte)((x4 >> 11) & 0xff);
        data[dataIndex] = (byte)((x4 >> 3) & 0xff);
        break;
      case 5:
        final int x3 = (decodeTable[encoded[i] & 0xff]) << 20
                     | (decodeTable[encoded[i + 1] & 0xff]) << 15
                     | (decodeTable[encoded[i + 2] & 0xff]) << 10
                     | (decodeTable[encoded[i + 3] & 0xff]) <<  5
                     | (decodeTable[encoded[i + 4] & 0xff]);
        data[dataIndex++] = (byte)((x3 >> 17) & 0xff);
        data[dataIndex++] = (byte)((x3 >> 9) & 0xff);
        data[dataIndex] = (byte)((x3 >> 1) & 0xff);
        break;
      case 4:
        final int x2 = (decodeTable[encoded[i] & 0xff]) << 15
                     | (decodeTable[encoded[i + 1] & 0xff]) << 10
                     | (decodeTable[encoded[i + 2] & 0xff]) <<  5
                     | (decodeTable[encoded[i + 3] & 0xff]);
        data[dataIndex++] = (byte)((x2 >> 12) & 0xff);
        data[dataIndex] = (byte)((x2 >> 4) & 0xff);
        break;
      case 2:
        final int x1 = (decodeTable[encoded[i] & 0xff]) << 5
                     | (decodeTable[encoded[i + 1] & 0xff]);
        data[dataIndex] = (byte)((x1 >> 2) & 0xff);
        break;
    }

    return data;
  }

  // ====================================================================================================
  //  Decode String related
  // ====================================================================================================
  public byte[] decode(final String encoded) {
    return decode(encoded, 0, StringUtil.length(encoded));
  }

  public byte[] decode(final String encoded, final int offset, int length) {
    if (StringUtil.isEmpty(encoded)) return null;

    final byte[] data = new byte[lengthDecoded(length)];
    int dataIndex = 0;

    int i = offset;
    while (length >= 8) {
      final long x5 = ((long)(decodeTable[encoded.charAt(i)])) << 35
                    | ((long)(decodeTable[encoded.charAt(i + 1)])) << 30
                    | ((long)(decodeTable[encoded.charAt(i + 2)])) << 25
                    | ((long)(decodeTable[encoded.charAt(i + 3)])) << 20
                    | ((long)(decodeTable[encoded.charAt(i + 4)])) << 15
                    | ((long)(decodeTable[encoded.charAt(i + 5)])) << 10
                    | ((long)(decodeTable[encoded.charAt(i + 6)])) <<  5
                    | (decodeTable[encoded.charAt(i + 7)]);
          data[dataIndex++] = (byte)((x5 >> 32) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 24) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 16) & 0xff);
          data[dataIndex++] = (byte)((x5 >> 8) & 0xff);
          data[dataIndex++] = (byte)((x5) & 0xff);

      i += 8;
      length -= 8;
    }

    switch (length) {
      case 7:
        final long x4 = ((long)(decodeTable[encoded.charAt(i)])) << 30
                      | ((long)(decodeTable[encoded.charAt(i + 1)])) << 25
                      | ((long)(decodeTable[encoded.charAt(i + 2)])) << 20
                      | ((long)(decodeTable[encoded.charAt(i + 3)])) << 15
                      | ((long)(decodeTable[encoded.charAt(i + 4)])) << 10
                      | ((long)(decodeTable[encoded.charAt(i + 5)])) <<  5
                      | (decodeTable[encoded.charAt(i + 6)]);
        data[dataIndex++] = (byte)((x4 >> 27) & 0xff);
        data[dataIndex++] = (byte)((x4 >> 19) & 0xff);
        data[dataIndex++] = (byte)((x4 >> 11) & 0xff);
        data[dataIndex] = (byte)((x4 >> 3) & 0xff);
        break;
      case 5:
        final int x3 = (decodeTable[encoded.charAt(i)]) << 20
                     | (decodeTable[encoded.charAt(i + 1)]) << 15
                     | (decodeTable[encoded.charAt(i + 2)]) << 10
                     | (decodeTable[encoded.charAt(i + 3)]) <<  5
                     | (decodeTable[encoded.charAt(i + 4)]);
        data[dataIndex++] = (byte)((x3 >> 17) & 0xff);
        data[dataIndex++] = (byte)((x3 >> 9) & 0xff);
        data[dataIndex] = (byte)((x3 >> 1) & 0xff);
        break;
      case 4:
        final int x2 = (decodeTable[encoded.charAt(i)]) << 15
                     | (decodeTable[encoded.charAt(i + 1)]) << 10
                     | (decodeTable[encoded.charAt(i + 2)]) <<  5
                     | (decodeTable[encoded.charAt(i + 3)]);
        data[dataIndex++] = (byte)((x2 >> 12) & 0xff);
        data[dataIndex] = (byte)((x2 >> 4) & 0xff);
        break;
      case 2:
        final int x1 = (decodeTable[encoded.charAt(i)]) << 5
                     | (decodeTable[encoded.charAt(i + 1)]);
        data[dataIndex] = (byte)((x1 >> 2) & 0xff);
        break;
    }

    return data;
  }

  private static int lengthDecoded(final int encodedLength) {
    final int length = (encodedLength >>> 3) * 5;
    return switch (encodedLength & 7) {
      case 7 -> length + 4;
      case 5 -> length + 3;
      case 4 -> length + 2;
      case 2 -> length + 1;
      default -> length;
    };
  }
}
