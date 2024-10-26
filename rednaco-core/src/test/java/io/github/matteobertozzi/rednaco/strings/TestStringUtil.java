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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.strings.StringUtil.LimitExceededException;

public class TestStringUtil {
  // ================================================================================
  //  String length related
  // ================================================================================
  @Test
  public void testLength() {
    Assertions.assertEquals(0, StringUtil.length(null));
    Assertions.assertEquals(0, StringUtil.length(""));
    Assertions.assertEquals(3, StringUtil.length("abc"));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(StringUtil.isEmpty(null));
    Assertions.assertTrue(StringUtil.isEmpty(""));

    Assertions.assertFalse(StringUtil.isNotEmpty(null));
    Assertions.assertFalse(StringUtil.isNotEmpty(""));

    Assertions.assertFalse(StringUtil.isEmpty("abc"));
    Assertions.assertTrue(StringUtil.isNotEmpty("abc"));
  }

  // ================================================================================
  //  String value related
  // ================================================================================
  @Test
  public void testNullAndEmptyConversion() {
    Assertions.assertEquals("", StringUtil.emptyIfNull(null));
    Assertions.assertEquals("", StringUtil.emptyIfNull(""));
    Assertions.assertEquals("abc", StringUtil.emptyIfNull("abc"));

    Assertions.assertNull(StringUtil.nullIfEmpty(null));
    Assertions.assertNull(StringUtil.nullIfEmpty(""));
    Assertions.assertEquals("abc", StringUtil.nullIfEmpty("abc"));
  }

  // ================================================================================
  //  String contains related
  // ================================================================================
  @Test
  public void testContains() {
    Assertions.assertFalse(StringUtil.contains(null, null));
    Assertions.assertFalse(StringUtil.contains(null, "foo"));

    Assertions.assertFalse(StringUtil.contains("", ""));
    Assertions.assertFalse(StringUtil.contains("", "foo"));

    Assertions.assertFalse(StringUtil.contains("hello", "foo"));
    Assertions.assertTrue(StringUtil.contains("hello foo", "foo"));
    Assertions.assertTrue(StringUtil.contains("hello foobar", "foo"));
  }

  @Test
  public void testStartsWith() {
    Assertions.assertFalse(StringUtil.startsWith(null, null));
    Assertions.assertFalse(StringUtil.startsWith("", null));
    Assertions.assertTrue(StringUtil.startsWith("", ""));
    Assertions.assertTrue(StringUtil.startsWith("foo", ""));
    Assertions.assertFalse(StringUtil.startsWith("foo", null));
    Assertions.assertFalse(StringUtil.startsWith("foo", "bar"));
    Assertions.assertTrue(StringUtil.startsWith("fooz", "foo"));
  }

  @Test
  public void testEndsWith() {
    Assertions.assertFalse(StringUtil.endsWith(null, null));
    Assertions.assertFalse(StringUtil.endsWith("", null));
    Assertions.assertTrue(StringUtil.endsWith("", ""));
    Assertions.assertTrue(StringUtil.endsWith("foo", ""));
    Assertions.assertFalse(StringUtil.endsWith("foo", null));
    Assertions.assertFalse(StringUtil.endsWith("foo", "bar"));
    Assertions.assertTrue(StringUtil.endsWith("zfoo", "foo"));
  }

  @Test
  public void testPrefix() {
    Assertions.assertEquals(0, StringUtil.prefix("", ""));

    Assertions.assertEquals(3, StringUtil.prefix("abc", "abcdef"));
    Assertions.assertEquals(3, StringUtil.prefix("abcdef", "abc"));
    Assertions.assertEquals(3, StringUtil.prefix("abc", "abc"));

    Assertions.assertEquals(2, StringUtil.prefix("abc", "abdef"));
    Assertions.assertEquals(2, StringUtil.prefix("abcde", "abd"));
    Assertions.assertEquals(2, StringUtil.prefix("abc", "abd"));

    Assertions.assertEquals(1, StringUtil.prefix("abc", "adef"));
    Assertions.assertEquals(1, StringUtil.prefix("abcd", "ade"));
    Assertions.assertEquals(1, StringUtil.prefix("abc", "ade"));

    Assertions.assertEquals(0, StringUtil.prefix("abc", "defg"));
    Assertions.assertEquals(0, StringUtil.prefix("abce", "def"));
    Assertions.assertEquals(0, StringUtil.prefix("abc", "def"));
  }

  @Test
  public void testSuffix() {
    Assertions.assertEquals(0, StringUtil.prefix("", ""));

    Assertions.assertEquals(3, StringUtil.suffix("axyz", "xyz"));
    Assertions.assertEquals(3, StringUtil.suffix("xyz", "axyz"));
    Assertions.assertEquals(3, StringUtil.suffix("xyz", "xyz"));

    Assertions.assertEquals(2, StringUtil.suffix("axyz", "wyz"));
    Assertions.assertEquals(2, StringUtil.suffix("xyz", "awyz"));
    Assertions.assertEquals(2, StringUtil.suffix("xyz", "wyz"));

    Assertions.assertEquals(1, StringUtil.suffix("axyz", "vwz"));
    Assertions.assertEquals(1, StringUtil.suffix("xyz", "avwz"));
    Assertions.assertEquals(1, StringUtil.suffix("xyz", "vwz"));

    Assertions.assertEquals(0, StringUtil.suffix("axyz", "uvw"));
    Assertions.assertEquals(0, StringUtil.suffix("xyz", "auvw"));
    Assertions.assertEquals(0, StringUtil.suffix("xyz", "uvw"));
  }

  // ================================================================================
  //  String upper/lower/capitalize case related
  // ================================================================================
  @Test
  public void testToUpper() {
    Assertions.assertNull(StringUtil.toUpper(null));
    Assertions.assertEquals("", StringUtil.toUpper(""));
    Assertions.assertEquals("UPPER", StringUtil.toUpper("upper"));
    Assertions.assertEquals("UPPER", StringUtil.toUpper("uPpEr"));
    Assertions.assertEquals("UPPER", StringUtil.toUpper("UpPeR"));
    Assertions.assertEquals("UPPER", StringUtil.toUpper("UPPER"));
  }

  @Test
  public void testToLower() {
    Assertions.assertNull(StringUtil.toLower(null));
    Assertions.assertEquals("", StringUtil.toLower(""));
    Assertions.assertEquals("lower", StringUtil.toLower("lower"));
    Assertions.assertEquals("lower", StringUtil.toLower("lOweR"));
    Assertions.assertEquals("lower", StringUtil.toLower("LoWeR"));
    Assertions.assertEquals("lower", StringUtil.toLower("LOWER"));
  }

  @Test
  public void testCapitalize() {
    Assertions.assertNull(StringUtil.capitalize(null));
    Assertions.assertEquals("", StringUtil.capitalize(""));

    Assertions.assertEquals("Hello", StringUtil.capitalize("hello"));
    Assertions.assertEquals("Hello", StringUtil.capitalize("hElLo", true));
    Assertions.assertEquals("Hello", StringUtil.capitalize("HeLlO", true));
    Assertions.assertEquals("HElLo", StringUtil.capitalize("hElLo", false));
    Assertions.assertEquals("HeLlO", StringUtil.capitalize("HeLlO", false));
  }

  // ================================================================================
  //  Pad helpers
  // ================================================================================
  @Test
  public void testFill() {
    Assertions.assertEquals("*****", StringUtil.fill('*', 5));
  }

  @Test
  public void testNumbersPadLeft() throws LimitExceededException {
    Assertions.assertEquals("00001", StringUtil.padLeft(1, '0', 5));
    Assertions.assertEquals("00012", StringUtil.padLeft(12, '0', 5));
    Assertions.assertEquals("0123", StringUtil.padLeft(123, '0', 4));
    Assertions.assertEquals("1234", StringUtil.padLeft(1234, '0', 4));
    try {
      StringUtil.padLeft(12345, '0', 4);
      Assertions.fail("expected LimitExceededException");
    } catch (final LimitExceededException e) {
      Assertions.assertEquals(1, e.excess());
    }
  }

  @Test
  public void testPadLeft() throws LimitExceededException {
    Assertions.assertEquals("00001", StringUtil.padLeft("1", '0', 5));
    Assertions.assertEquals("00012", StringUtil.padLeft("12", '0', 5));
    Assertions.assertEquals("0123", StringUtil.padLeft("123", '0', 4));
    Assertions.assertEquals("1234", StringUtil.padLeft("1234", '0', 4));
    try {
      StringUtil.padLeft("12345", '0', 4);
      Assertions.fail("expected LimitExceededException");
    } catch (final LimitExceededException e) {
      Assertions.assertEquals(1, e.excess());
    }
  }

  @Test
  public void testNumbersPadRight() throws LimitExceededException {
    Assertions.assertEquals("10000", StringUtil.padRight(1, '0', 5));
    Assertions.assertEquals("12000", StringUtil.padRight(12, '0', 5));
    Assertions.assertEquals("1230", StringUtil.padRight(123, '0', 4));
    Assertions.assertEquals("1234", StringUtil.padRight(1234, '0', 4));
    try {
      StringUtil.padRight(12345, '0', 4);
      Assertions.fail("expected LimitExceededException");
    } catch (final LimitExceededException e) {
      // expected
    }
  }

  @Test
  public void testPadRight() throws LimitExceededException {
    Assertions.assertEquals("10000", StringUtil.padRight("1", '0', 5));
    Assertions.assertEquals("12000", StringUtil.padRight("12", '0', 5));
    Assertions.assertEquals("1230", StringUtil.padRight("123", '0', 4));
    Assertions.assertEquals("1234", StringUtil.padRight("1234", '0', 4));
    try {
      StringUtil.padRight("12345", '0', 4);
      Assertions.fail("expected LimitExceededException");
    } catch (final LimitExceededException e) {
      // expected
    }
  }

  // ================================================================================
  //  String trim related
  // ================================================================================
  @Test
  public void testTrim() {
    Assertions.assertNull(StringUtil.trim(null));
    Assertions.assertEquals("", StringUtil.trim(""));
    Assertions.assertEquals("", StringUtil.trim("   "));
    Assertions.assertEquals("", StringUtil.trim(" \t "));
    Assertions.assertEquals("abc \t  def", StringUtil.trim(" \t abc \t  def \t \n "));

    Assertions.assertEquals("", StringUtil.trimToEmpty(null));
    Assertions.assertEquals("", StringUtil.trimToEmpty(""));
    Assertions.assertEquals("", StringUtil.trimToEmpty("   "));
    Assertions.assertEquals("", StringUtil.trimToEmpty(" \t "));
    Assertions.assertEquals("abc \t  def", StringUtil.trimToEmpty(" \t abc \t  def \t \n "));

    Assertions.assertNull(StringUtil.trimToNull(null));
    Assertions.assertNull(StringUtil.trimToNull(""));
    Assertions.assertNull(StringUtil.trimToNull("   "));
    Assertions.assertNull(StringUtil.trimToNull(" \t "));
    Assertions.assertEquals("abc \t  def", StringUtil.trimToNull(" \t abc \t  def \t \n "));

    Assertions.assertNull(StringUtil.ltrim(null));
    Assertions.assertEquals("", StringUtil.ltrim(""));
    Assertions.assertEquals("", StringUtil.ltrim("   "));
    Assertions.assertEquals("abc  ", StringUtil.ltrim("  \t  abc  "));
    Assertions.assertEquals("abc \t  ", StringUtil.ltrim("abc \t  "));

    Assertions.assertNull(StringUtil.rtrim(null));
    Assertions.assertEquals("", StringUtil.rtrim(""));
    Assertions.assertEquals("", StringUtil.rtrim("   "));
    Assertions.assertEquals("  \t  abc", StringUtil.rtrim("  \t  abc  "));
    Assertions.assertEquals("abc", StringUtil.rtrim("abc \t  "));
  }

  @Test
  public void testCollpaseSpaces() {
    Assertions.assertNull(StringUtil.collapseSpaces(null));
    Assertions.assertEquals("", StringUtil.collapseSpaces(""));
    Assertions.assertEquals(" aaa bbb ccc ", StringUtil.collapseSpaces("  aaa    bbb   ccc "));
    Assertions.assertEquals(" aaa bbb ccc ", StringUtil.collapseSpaces("  aaa  \t \t\t  bbb  \t\n\t ccc \t\n"));
  }

  @Test
  public void testSplitAndTrim() {
    Assertions.assertNull(StringUtil.splitAndTrim(null, "/"));
    Assertions.assertNull(StringUtil.splitAndTrim("", "/"));
    Assertions.assertArrayEquals(new String[] { "abc" }, StringUtil.splitAndTrim("abc", "/"));
    Assertions.assertArrayEquals(new String[] { "abc", "def" }, StringUtil.splitAndTrim("abc/ def", "/"));
    Assertions.assertArrayEquals(new String[] { "", "abc", "def" }, StringUtil.splitAndTrim("/abc/def /", "/"));
    Assertions.assertArrayEquals(new String[] { "", "abc", "", "def" }, StringUtil.splitAndTrim("/abc//def/", "/"));
    Assertions.assertArrayEquals(new String[] { "", "abc", "", "def" }, StringUtil.splitAndTrim("/abc/ /def/", "/"));

    Assertions.assertNull(StringUtil.splitAndTrimSkipEmptyLines(null, "/"));
    Assertions.assertNull(StringUtil.splitAndTrimSkipEmptyLines("", "/"));
    Assertions.assertArrayEquals(new String[] { "abc" }, StringUtil.splitAndTrimSkipEmptyLines("abc", "/"));
    Assertions.assertArrayEquals(new String[] { "abc", "def" }, StringUtil.splitAndTrimSkipEmptyLines("abc/ def", "/"));
    Assertions.assertArrayEquals(new String[] { "abc", "def" }, StringUtil.splitAndTrimSkipEmptyLines("/abc/def /", "/"));
    Assertions.assertArrayEquals(new String[] { "abc", "def" }, StringUtil.splitAndTrimSkipEmptyLines("/abc//def/", "/"));
    Assertions.assertArrayEquals(new String[] { "abc", "def" }, StringUtil.splitAndTrimSkipEmptyLines("/abc/ /def/", "/"));
  }

  @Test
  public void testSplitAndTrimSingleChar() {
    Assertions.assertEquals(List.of(), StringUtil.splitAndTrim(null, '/'));
    Assertions.assertEquals(List.of(), StringUtil.splitAndTrim("", '/'));
    Assertions.assertEquals(List.of("abc"), StringUtil.splitAndTrim("abc", '/'));
    Assertions.assertEquals(List.of("abc", "def"), StringUtil.splitAndTrim("abc/ def", '/'));
    Assertions.assertEquals(List.of("", "abc", "def", ""), StringUtil.splitAndTrim("/abc/def /", '/'));
    Assertions.assertEquals(List.of("", "abc", "", "def", ""), StringUtil.splitAndTrim("/abc//def/", '/'));
    Assertions.assertEquals(List.of("", "abc", "", "def", ""), StringUtil.splitAndTrim("/abc/ /def/", '/'));

    Assertions.assertEquals(List.of(), StringUtil.splitAndTrimSkipEmptyLines(null, '/'));
    Assertions.assertEquals(List.of(), StringUtil.splitAndTrimSkipEmptyLines("", '/'));
    Assertions.assertEquals(List.of("abc"), StringUtil.splitAndTrimSkipEmptyLines("abc", '/'));
    Assertions.assertEquals(List.of("abc", "def"), StringUtil.splitAndTrimSkipEmptyLines("abc/ def", '/'));
    Assertions.assertEquals(List.of("abc", "def"), StringUtil.splitAndTrimSkipEmptyLines("/abc/def /", '/'));
    Assertions.assertEquals(List.of("abc", "def"), StringUtil.splitAndTrimSkipEmptyLines("/abc//def/", '/'));
    Assertions.assertEquals(List.of("abc", "def"), StringUtil.splitAndTrimSkipEmptyLines("/abc/ /def/", '/'));
  }

  @Test
  public void testSplitSingleChar() {
    Assertions.assertEquals(List.of(), StringUtil.split(null, '/'));
    Assertions.assertEquals(List.of(), StringUtil.split("", '/'));
    Assertions.assertEquals(List.of("abc"), StringUtil.split("abc", '/'));
    Assertions.assertEquals(List.of("abc", " def"), StringUtil.split("abc/ def", '/'));
    Assertions.assertEquals(List.of("", "abc", "def ", ""), StringUtil.split("/abc/def /", '/'));
    Assertions.assertEquals(List.of("", "abc", "", "def", ""), StringUtil.split("/abc//def/", '/'));
    Assertions.assertEquals(List.of("", "abc", " ", "def", ""), StringUtil.split("/abc/ /def/", '/'));
  }

  // ================================================================================
  //  String comparison related
  // ================================================================================
  @Test
  public void testEquals() {
    Assertions.assertTrue(StringUtil.equals(null, null));
    Assertions.assertFalse(StringUtil.equals(null, ""));
    Assertions.assertFalse(StringUtil.equals("", null));
    Assertions.assertTrue(StringUtil.equals("", ""));
    Assertions.assertFalse(StringUtil.equals(null, "abc"));
    Assertions.assertFalse(StringUtil.equals("abc", null));
  }
}
