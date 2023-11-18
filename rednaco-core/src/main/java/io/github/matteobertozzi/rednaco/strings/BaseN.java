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

import java.util.Arrays;

import io.github.matteobertozzi.rednaco.bytes.BytesUtil;

public class BaseN {
  private static final class HolderBase58 {
    private static final BaseN BASE58 = new BaseN("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
  }
  private static final class HolderBase62 {
    private static final BaseN BASE62 = new BaseN("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
  }

  public static BaseN base58() { return HolderBase58.BASE58; }
  public static BaseN base62() { return HolderBase62.BASE62; }

  private final char[] alphabet;
  private final short[] decodeTable;

  public BaseN(final String alphabet) {
    this(alphabet.toCharArray());
  }

  public BaseN(final char[] alphabet) {
    this(alphabet, buildDecodeTable(alphabet));
  }

  public BaseN(final char[] alphabet, final short[] decodeTable) {
    this.alphabet = alphabet;
    this.decodeTable = decodeTable;
  }

  static short[] buildDecodeTable(final char[] alphabet) {
    final short[] table = new short[128];
    Arrays.fill(table, (short)-1);
    for (int i = 0; i < alphabet.length; ++i) {
      table[alphabet[i]] = (short)i;
    }
    return table;
  }

  public String encode(final long value) {
    return encode(alphabet, value);
  }

  public static String encode(final char[] alphabet, final long value) {
    final StringBuilder builder = new StringBuilder();
    final int base = alphabet.length;
    long remaining = value;
    do {
      final int d = (int) Long.remainderUnsigned(remaining, base);
      remaining = Long.divideUnsigned(remaining, base);

      builder.append(alphabet[d]);
    } while (remaining != 0);
    return builder.reverse().toString();
  }

  public String encode(final byte[] input) {
    if (input.length == 0) {
      return "";
    }

    int zeros = 0;
    while (zeros < input.length && input[zeros] == 0) {
      ++zeros;
    }

    final int base = alphabet.length;
    final byte[] numbers = Arrays.copyOf(input, input.length);
    final char[] encoded = new char[numbers.length * 2];
    int outputStart = encoded.length;
    for (int inputStart = zeros; inputStart < numbers.length;) {
      encoded[--outputStart] = alphabet[divmod(numbers, inputStart, base)];
      if (numbers[inputStart] == 0) {
        ++inputStart;
      }
    }

    while (outputStart < encoded.length && encoded[outputStart] == alphabet[0]) {
      ++outputStart;
    }
    while (--zeros >= 0) {
      encoded[--outputStart] = alphabet[0];
    }

    return new String(encoded, outputStart, encoded.length - outputStart);
  }

  public byte[] decode(final String input) {
    return decode(input, 0, StringUtil.length(input));
  }

  public byte[] decode(final String input, final int off, final int len) {
    if (len == 0) {
      return BytesUtil.EMPTY_BYTES;
    }

    final byte[] numbers = mapInput(input, off, len);

    int zeros = 0;
    while (zeros < numbers.length && numbers[zeros] == 0) {
      ++zeros;
    }

    final int base = alphabet.length;
    final byte[] decoded = new byte[numbers.length];
    int outputStart = decoded.length;
    for (int inputStart = zeros; inputStart < numbers.length;) {
      decoded[--outputStart] = divmod256(numbers, inputStart, base);
      if (numbers[inputStart] == 0) {
        ++inputStart;
      }
    }

    while (outputStart < decoded.length && decoded[outputStart] == 0) {
      ++outputStart;
    }

    final int offset = outputStart - zeros;
    if (offset == 0) {
      return decoded;
    }
    return Arrays.copyOfRange(decoded, offset, decoded.length);
  }

  private byte[] mapInput(final String input, final int off, final int len) {
    final byte[] numbers = new byte[len];
    for (int i = 0; i < len; ++i) {
      final char c = input.charAt(off + i);
      final int digit = c < 128 ? decodeTable[c] & 0xff : -1;
      if (digit < 0) {
        throw new IllegalArgumentException(String.format("Invalid character: 0x%04x", (int) c));
      }
      numbers[i] = (byte) digit;
    }
    return numbers;
  }

  private static byte divmod(final byte[] number, final int firstDigit, final int divisor) {
    int remainder = 0;
    for (int i = firstDigit; i < number.length; i++) {
      final int digit = number[i] & 0xFF;
      final int temp = (remainder << 8) + digit;
      number[i] = (byte) (temp / divisor);
      remainder = temp % divisor;
    }
    return (byte)remainder;
  }

  private static byte divmod256(final byte[] number, final int firstDigit, final int base) {
    int remainder = 0;
    for (int i = firstDigit; i < number.length; ++i) {
      final int digit = number[i] & 0xFF;
      final int temp = remainder * base + digit;
      number[i] = (byte) ((temp >>> 8) & 0xff);
      remainder = temp & 255;
    }
    return (byte)remainder;
  }
}
