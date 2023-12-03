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

import java.util.Arrays;

public final class BytesSearch {
  private BytesSearch() {
    // no-op
  }

  // ================================================================================
  //  Bytes find single byte util
  // ================================================================================
  public static int indexOf(final byte[] haystack, final byte needle) {
    return indexOf(haystack, 0, haystack.length, needle);
  }

  public static int indexOf(final byte[] haystack, final int haystackOff, final byte needle) {
    return indexOf(haystack, haystackOff, haystack.length - haystackOff, needle);
  }

  public static int indexOf(final byte[] haystack, final int haystackOff, final int haystackLen, final byte needle) {
    for (int i = 0; i < haystackLen; ++i) {
      if (haystack[haystackOff + i] == needle) {
        return haystackOff + i;
      }
    }
    return -1;
  }

  public static int lastIndexOf(final byte[] haystack, final byte needle) {
    return lastIndexOf(haystack, 0, haystack.length, needle);
  }

  public static int lastIndexOf(final byte[] haystack, final int haystackOff, final byte needle) {
    return lastIndexOf(haystack, haystackOff, haystack.length - haystackOff, needle);
  }

  public static int lastIndexOf(final byte[] haystack, final int haystackOff, final int haystackLen, final byte needle) {
    for (int i = haystackLen - 1; i >= 0; --i) {
      if (haystack[haystackOff + i] == needle) {
        return haystackOff + i;
      }
    }
    return -1;
  }

  // ================================================================================
  //  Bytes find multi byte util
  // ================================================================================
  public static int indexOf(final byte[] haystack, final byte[] needle) {
    return indexOf(haystack, 0, haystack.length, needle, needle.length);
  }

  public static int indexOf(final byte[] haystack, final int haystackOff, final byte[] needle) {
    return indexOf(haystack, haystackOff, haystack.length - haystackOff, needle, needle.length);
  }

  public static int indexOf(final byte[] haystack, final int haystackOff, final int haystackLen, final byte[] needle) {
    return indexOf(haystack, haystackOff, haystackLen, needle, needle.length);
  }

  private static int indexOf(final byte[] haystack, final int haystackOff, final int haystackLen,
      final byte[] needle, final int needleLen) {
    if (needleLen > haystackLen || needleLen == 0 || haystackLen == 0) return -1;
    if (needleLen == 1) return indexOf(haystack, haystackOff, haystackLen, needle[0]);

    final int len = haystackLen - needleLen;
    for (int i = 0; i <= len; ++i) {
      final int off = haystackOff + i;
      if (Arrays.equals(haystack, off, off + needleLen, needle, 0, needleLen)) {
        return off;
      }
    }
    return -1;
  }

  public static int lastIndexOf(final byte[] haystack, final byte[] needle) {
    return lastIndexOf(haystack, 0, haystack.length, needle, needle.length);
  }

  public static int lastIndexOf(final byte[] haystack, final int haystackOff, final byte[] needle) {
    return lastIndexOf(haystack, haystackOff, haystack.length - haystackOff, needle, needle.length);
  }

  public static int lastIndexOf(final byte[] haystack, final int haystackOff, final int haystackLen, final byte[] needle) {
    return lastIndexOf(haystack, haystackOff, haystackLen, needle, needle.length);
  }

  private static int lastIndexOf(final byte[] haystack, final int haystackOff, final int haystackLen,
      final byte[] needle, final int needleLen) {
    if (needleLen > haystackLen || needleLen == 0 || haystackLen == 0) return -1;
    if (needleLen == 1) return lastIndexOf(haystack, haystackOff, haystackLen, needle[0]);

    final int len = haystackLen - needleLen;
    for (int i = len; i >= 0; --i) {
      final int off = haystackOff + i;
      if (Arrays.equals(haystack, off, off + needleLen, needle, 0, needleLen)) {
        return off;
      }
    }
    return -1;
  }

  // ================================================================================
  //  Bytes Prefix related
  // ================================================================================
  public static int prefix(final byte[] a, final byte[] b) {
    return prefix(a, 0, a.length, b, 0, b.length);
  }

  public static int prefix(final byte[] a, final int aOff, final int aLen,
      final byte[] b, final int bOff, final int bLen) {
    final int len = Math.min(aLen, bLen);
    for (int i = 0; i < len; ++i) {
      if (a[aOff + i] != b[bOff + i]) {
        return i;
      }
    }
    return len;
  }

  public static boolean hasPrefix(final byte[] buf, final byte[] prefix) {
    return hasPrefix(buf, 0, BytesUtil.length(buf), prefix, 0, BytesUtil.length(prefix));
  }

  public static boolean hasPrefix(final byte[] buf, final int off, final int len,
      final byte[] prefix, final int prefixOff, final int prefixLen) {
    return prefix(buf, off, len, prefix, prefixOff, prefixLen) == prefixLen;
  }

  // ================================================================================
  //  Bytes Suffix related
  // ================================================================================
  public static int suffix(final byte[] a, final byte[] b) {
    return suffix(a, 0, a.length, b, 0, b.length);
  }

  public static int suffix(final byte[] a, final int aOff, final int aLen,
      final byte[] b, final int bOff, final int bLen) {
    final int len = Math.min(aLen, bLen);
    for (int i = 1; i <= len; ++i) {
      if ((a[aLen - i] & 0xff) != (b[bLen - i] & 0xff)) {
        return i - 1;
      }
    }
    return len;
  }
}
