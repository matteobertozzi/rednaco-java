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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public final class StringUtil {
  public static final char[] ALPHA_NUMERIC_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
  public static final char[] ASCII_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  public static final char[] ASCII_LOWERCASE = "abcdefghijklmnopqrstuvwxyz".toCharArray();
  public static final char[] HEX_DIGITS = "0123456789abcdefABCDEF".toCharArray();

  public static final String[] EMPTY_ARRAY = new String[0];

  private StringUtil() {
    // no-op
  }

  // ================================================================================
  //  String length related
  // ================================================================================
  public static int length(final String input) {
    return input == null ? 0 : input.length();
  }

  public static boolean isEmpty(final String input) {
    return (input == null) || input.isEmpty();
  }

  public static boolean isNotEmpty(final String input) {
    return (input != null) && !input.isEmpty();
  }

  // ================================================================================
  //  String value related
  // ================================================================================
  public static String emptyIfNull(final String input) {
    return input == null ? "" : input;
  }

  public static String nullIfEmpty(final String input) {
    return isEmpty(input) ? null : input;
  }

  public static String defaultIfEmpty(final String input, final String defaultValue) {
    return isNotEmpty(input) ? input : defaultValue;
  }

  // ================================================================================
  //  String contains related
  // ================================================================================
  public static boolean contains(final String text, final String pattern) {
    return StringUtil.isNotEmpty(text) && text.contains(pattern);
  }

  public static boolean startsWith(final String text, final String prefix) {
    return text != null && prefix != null && text.startsWith(prefix);
  }

  public static boolean endsWith(final String text, final String suffix) {
    return text != null && suffix != null && text.endsWith(suffix);
  }

  public static int prefix(final String a, final String b) {
    final int len = Math.min(a.length(), b.length());
    for (int i = 0; i < len; ++i) {
      if (a.charAt(i) != b.charAt(i)) {
        return i;
      }
    }
    return len;
  }

  public static int prefix(final String a, final int aOff, final int aLen, final String b, final int bOff, final int bLen) {
    final int len = Math.min(aLen, bLen);
    for (int i = 0; i < len; ++i) {
      if (a.charAt(aOff + i) != b.charAt(bOff + i)) {
        return i;
      }
    }
    return len;
  }

  public static int suffix(final String a, final String b) {
    final int aLen = a.length();
    final int bLen = b.length();
    final int len = Math.min(aLen, bLen);
    for (int i = 1; i <= len; ++i) {
      if (a.charAt(aLen - i) != b.charAt(bLen - i)) {
        return i - 1;
      }
    }
    return len;
  }

  // ================================================================================
  //  String upper/lower/capitalize case related
  // ================================================================================
  public static String toUpper(final String value) {
    return value != null ? value.toUpperCase() : null;
  }

  public static String toLower(final String value) {
    return value != null ? value.toLowerCase() : null;
  }

  public static String capitalize(final String value) {
    return capitalize(value, true);
  }

  public static String capitalize(final String value, final boolean everythingLower) {
    if (StringUtil.isEmpty(value)) return value;

    final StringBuilder sb = new StringBuilder(value.length());
    sb.append(Character.toUpperCase(value.charAt(0)));
    for (int i = 1; i < value.length(); ++i) {
      if (everythingLower) {
        sb.append(Character.toLowerCase(value.charAt(i)));
      } else {
        sb.append(value.charAt(i));
      }
    }
    return sb.toString();
  }

  // ================================================================================
  //  Pad helpers
  // ================================================================================
  public static String fill(final char ch, final int length) {
    final char[] pad = new char[length];
    Arrays.fill(pad, ch);
    return new String(pad);
  }

  public static String padLeft(final int data, final char padCh, final int length) throws LimitExceededException {
    return padLeft(Integer.toString(data), padCh, length);
  }

  public static String padLeft(final String data, final char padCh, final int length) throws LimitExceededException {
    return pad(data, padCh, length, true);
  }

  public static String padRight(final int data, final char padCh, final int length) throws LimitExceededException {
    return padRight(Integer.toString(data), padCh, length);
  }

  public static String padRight(final String data, final char padCh, final int length) throws LimitExceededException {
    return pad(data, padCh, length, false);
  }

  private static String pad(final String data, final char padCh, final int length, final boolean padLeft) throws LimitExceededException {
    if (StringUtil.isEmpty(data)) {
      return fill(padCh, length);
    }

    if (data.length() >= length) {
      if (data.length() > length) {
        throw new LimitExceededException(length, data);
      }
      return data;
    }

    final char[] pad = new char[length - data.length()];
    Arrays.fill(pad, padCh);

    final StringBuilder sb = new StringBuilder(length);
    if (padLeft) {
      sb.append(pad).append(data);
    } else {
      sb.append(data).append(pad);
    }
    return sb.toString();
  }

  public static final class LimitExceededException extends Exception {
    private final int excess;

    public LimitExceededException(final int expectedLength, final String data) {
      super(String.format("limit exceeded: expected %d got %d: '%s'", expectedLength, data.length(), data));
      this.excess = data.length() - expectedLength;
    }

    public int excess() {
      return this.excess;
    }
  }

  // ================================================================================
  //  String trim related
  // ================================================================================
  public static String trim(final String input) {
    return isEmpty(input) ? input : input.trim();
  }

  public static String trimToEmpty(final String input) {
    return isEmpty(input) ? "" : input.trim();
  }

  public static String trimToNull(final String input) {
    return isEmpty(input) ? null : nullIfEmpty(input.trim());
  }

  public static String ltrim(final String input) {
    if (input == null) return null;
    final int length = input.length();
    int st = 0;
    while (st < length && Character.isWhitespace(input.charAt(st))) {
      st++;
    }
    return st > 0 ? input.substring(st) : input;
  }

  public static String rtrim(final String input) {
    if (input == null) return null;
    int length = input.length();
    while (length > 0 && Character.isWhitespace(input.charAt(length - 1))) {
      length--;
    }
    return length != input.length() ? input.substring(0, length) : input;
  }

  public static String collapseSpaces(final String input) {
    return isEmpty(input) ? input : input.replaceAll("\\s+", " ");
  }

  public static String[] splitAndTrim(final String input, final String delimiter) {
    final String itrim = input != null ? input.trim() : null;
    if (isEmpty(itrim)) return null;

    final String[] items = itrim.split(delimiter);
    for (int i = 0; i < items.length; ++i) {
      items[i] = items[i].trim();
    }
    return items;
  }

  public static String[] splitAndTrimSkipEmptyLines(final String input, final String delimiter) {
    final String itrim = input != null ? input.trim() : null;
    if (isEmpty(itrim)) return null;

    final String[] rawItems = itrim.split(delimiter);
    final ArrayList<String> items = new ArrayList<>(rawItems.length);
    for (int i = 0; i < rawItems.length; ++i) {
      final String row = StringUtil.trim(rawItems[i]);
      if (StringUtil.isNotEmpty(row)) {
        items.add(row);
      }
    }
    return items.toArray(new String[0]);
  }

  // ================================================================================
  //  String comparison related
  // ================================================================================
  public static final Comparator<String> STRING_REVERSE_COMPARATOR = (a, b) -> StringUtil.compare(b, a);

  @SuppressWarnings({ "StringEquality", "EqualsReplaceableByObjectsCall" })
  public static boolean equals(final String a, final String b) {
    return (a == b) || (a != null && a.equals(b));
  }

  @SuppressWarnings("StringEquality")
  public static boolean equalsIgnoreCase(final String a, final String b) {
    return (a == b) || (a != null && a.equalsIgnoreCase(b));
  }

  @SuppressWarnings("StringEquality")
  public static int compare(final String a, final String b) {
    if (a == b) return 0;
    if (a == null) return -1;
    if (b == null) return 1;
    return a.compareTo(b);
  }

  @Deprecated
  public static int compareTo(final String a, final String b) {
    return compare(a, b);
  }

  @SuppressWarnings("StringEquality")
  public static int compareIgnoreCase(final String a, final String b) {
    if (a == b) return 0;
    if (a == null) return -1;
    if (b == null) return 1;
    return a.compareToIgnoreCase(b);
  }

  public static boolean equals(final String a, final int aOff, final int aLen,
      final String b, final int bOff, final int bLen) {
    if (aLen != bLen) return false;

    for (int i = 0; i < aLen; ++i) {
      if (a.charAt(aOff + i) != b.charAt(bOff + i)) {
        return false;
      }
    }
    return true;
  }
}
