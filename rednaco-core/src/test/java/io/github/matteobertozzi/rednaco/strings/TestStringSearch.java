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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.strings.StringSearch.LikePattern.MatchType;

public class TestStringSearch {
  @Test
  public void testLike() {
    Assertions.assertTrue(StringSearch.like("", "%"));
    Assertions.assertTrue(StringSearch.like("", ""));
    Assertions.assertFalse(StringSearch.like(null, "%"));
    Assertions.assertFalse(StringSearch.like(null, ""));
    Assertions.assertFalse(StringSearch.like("", "abc\\_d"));

    Assertions.assertTrue(StringSearch.like("abc_d", "abc\\_d"));
    Assertions.assertTrue(StringSearch.like("abc%d", "abc\\%%d"));
    Assertions.assertFalse(StringSearch.like("abcd", "abc\\_d"));

    final String source = "1abcd";
    Assertions.assertTrue(StringSearch.like(source, "_%d"));
    Assertions.assertFalse(StringSearch.like(source, "%%a"));
    Assertions.assertFalse(StringSearch.like(source, "1"));
    Assertions.assertTrue(StringSearch.like(source, "%d"));
    Assertions.assertTrue(StringSearch.like(source, "%%%%"));
    Assertions.assertTrue(StringSearch.like(source, "1%_"));
    Assertions.assertFalse(StringSearch.like(source, "1%_2"));
    Assertions.assertFalse(StringSearch.like(source, "1abcdef"));
    Assertions.assertTrue(StringSearch.like(source, "1abcd"));
    Assertions.assertFalse(StringSearch.like(source, "1abcde"));

    Assertions.assertTrue(StringSearch.like(source, "_%_"));
    Assertions.assertTrue(StringSearch.like(source, "_%____"));
    Assertions.assertTrue(StringSearch.like(source, "_____"));
    Assertions.assertFalse(StringSearch.like(source, "___"));
    Assertions.assertFalse(StringSearch.like(source, "__%____"));
    Assertions.assertFalse(StringSearch.like(source, "1"));

    Assertions.assertFalse(StringSearch.like(source, "a_%b"));
    Assertions.assertTrue(StringSearch.like(source, "1%"));
    Assertions.assertFalse(StringSearch.like(source, "d%"));
    Assertions.assertTrue(StringSearch.like(source, "_%"));
    Assertions.assertTrue(StringSearch.like(source, "_abc%"));
    Assertions.assertTrue(StringSearch.like(source, "%d"));
    Assertions.assertTrue(StringSearch.like(source, "%abc%"));
    Assertions.assertFalse(StringSearch.like(source, "ab_%"));

    Assertions.assertTrue(StringSearch.like(source, "1ab__"));
    Assertions.assertTrue(StringSearch.like(source, "1ab__%"));
    Assertions.assertFalse(StringSearch.like(source, "1ab___"));
    Assertions.assertTrue(StringSearch.like(source, "%"));

    Assertions.assertFalse(StringSearch.like(null, "1ab___"));
    Assertions.assertFalse(StringSearch.like(source, null));
    Assertions.assertFalse(StringSearch.like(source, ""));

    final String source2 = "1abcd1abcdef1abc";
    Assertions.assertTrue(StringSearch.like(source2, "%abc%"));
    Assertions.assertTrue(StringSearch.like(source2, "%abcdef%"));
    Assertions.assertFalse(StringSearch.like(source2, "%abcdefg%"));

    // test regex escape
    Assertions.assertTrue(StringSearch.like("foo-bar", "%o-b%"));
    Assertions.assertTrue(StringSearch.like("foo,bar", "%o,b%"));
    Assertions.assertTrue(StringSearch.like("foo#bar", "%o#b%"));
    Assertions.assertTrue(StringSearch.like("foo & bar", "%o & b%"));
    Assertions.assertTrue(StringSearch.like("foo(bar)", "foo(_%"));
    Assertions.assertTrue(StringSearch.like("foo(bar)", "%bar)%"));
    Assertions.assertTrue(StringSearch.like("foo[bar]", "%[bar%"));
    Assertions.assertTrue(StringSearch.like("foo[bar]", "%bar]"));
    Assertions.assertTrue(StringSearch.like("foo{bar}", "%{bar%"));
    Assertions.assertTrue(StringSearch.like("foo{bar}", "%bar}"));
    Assertions.assertTrue(StringSearch.like("a + b + c", "%b + c%"));
    Assertions.assertTrue(StringSearch.like("a + b? + c", "%b?%"));
    Assertions.assertTrue(StringSearch.like("a + b|d + c", "%b|d%"));
    Assertions.assertTrue(StringSearch.like("a + b>d + c", "%b>d%"));
    Assertions.assertTrue(StringSearch.like("a + b<d + c", "%b<d%"));
    Assertions.assertTrue(StringSearch.like("a + b/d + c", "%b/d%"));
  }

  @Test
  public void testLikeType() {
    Assertions.assertEquals(MatchType.FULL, StringSearch.likePattern("foo").matchType());
    Assertions.assertEquals(MatchType.RANDOM, StringSearch.likePattern("_oo").matchType());
    Assertions.assertEquals(MatchType.RANDOM, StringSearch.likePattern("%o").matchType());
    Assertions.assertEquals(MatchType.PREFIX, StringSearch.likePattern("f_o").matchType());
    Assertions.assertEquals(MatchType.PREFIX, StringSearch.likePattern("f%").matchType());
    Assertions.assertEquals(MatchType.EVERYTHING, StringSearch.likePattern("%").matchType());
    Assertions.assertEquals(MatchType.EVERYTHING, StringSearch.likePattern("%%").matchType());
    Assertions.assertEquals(MatchType.RANDOM, StringSearch.likePattern("__").matchType());
    Assertions.assertEquals(MatchType.NOTHING, StringSearch.likePattern(null).matchType());
    Assertions.assertEquals(MatchType.FULL, StringSearch.likePattern("").matchType());
  }
}
