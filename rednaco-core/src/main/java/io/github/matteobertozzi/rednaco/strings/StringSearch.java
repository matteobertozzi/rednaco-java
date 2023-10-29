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

import java.util.regex.Pattern;

import io.github.matteobertozzi.rednaco.strings.StringSearch.LikePattern.MatchType;

public final class StringSearch {
  private StringSearch() {
    // no-op
  }

  // ================================================================================
  //  String like related
  // ================================================================================
  public static boolean like(final String source, final String exp) {
    if (source == null || exp == null) {
      return false;
    }

    if (source.isEmpty() && exp.isEmpty()) {
      return true;
    }

    return likePattern(exp).matches(source);
  }

  public static LikePattern likePattern(final String patternString) {
    if (patternString == null) return LikeWithNoMatch.INSTANCE;
    if (patternString.isEmpty()) return new LikeWithStringEquals("");

    final char ESCAPE_CHAR = '\\';
    int anythingWildcards = 0;
    int singleWildcards = 0;

    final StringBuilder regex = new StringBuilder(patternString.length() * 2);
    regex.append('^');
    boolean escaped = false;
    for (int i = 0, n = patternString.length(); i < n; ++i) {
      final char currentChar = patternString.charAt(i);
      if (!(!escaped || currentChar == '%' || currentChar == '_' || currentChar == ESCAPE_CHAR)) {
        throw new IllegalArgumentException("Escape character must be followed by '%%', '_' or the escape character itself");
      }
      //if (shouldEscape && !escaped && (currentChar == escapeChar)) {
      if (!escaped && (currentChar == ESCAPE_CHAR)) {
        escaped = true;
      } else {
        switch (currentChar) {
          case '%':
            regex.append(escaped ? "%" : ".*");
            escaped = false;
            anythingWildcards++;
            break;
          case '_':
            regex.append(escaped ? '_' : '.');
            escaped = false;
            singleWildcards++;
            break;
          default:
            // escape special regex characters
            // [-[\]{}()*+?.,\\^$|#\s]/g, '\\$&
            switch (currentChar) {
              case '\\':
              case '^':
              case '$':
              case '.':
              case '*':
              case '+':
              case '?':
              case '|':
              case '(':
              case ')':
              case '[':
              case ']':
              case '{':
              case '}':
                regex.append('\\');
                break;
            }
            regex.append(currentChar);
            escaped = false;
            break;
        }
      }
    }
    if (escaped) {
      throw new IllegalArgumentException("Escape character must be followed by '%%', '_' or the escape character itself");
    }
    regex.append('$');

    if (anythingWildcards == patternString.length()) {
      return LikeMatchingEverything.INSTANCE;
    } else if (anythingWildcards > 0 || singleWildcards > 0) {
      // if (singleWildcards == patternString.length()) return new LikeWithStringLengthEquals(singleWildCards);
      final char firstChar = patternString.charAt(0);
      final boolean prefixMatch = (firstChar != '%' && firstChar != '_');
      return new RegexLikePattern(Pattern.compile(regex.toString()), prefixMatch);
    }
    return new LikeWithStringEquals(patternString);
  }

  public interface LikePattern {
    enum MatchType { NOTHING, EVERYTHING, PREFIX, FULL, RANDOM }
    boolean matches(final String input);
    MatchType matchType();
  }

  private static final class LikeMatchingEverything implements LikePattern {
    private static final LikeMatchingEverything INSTANCE = new LikeMatchingEverything();
    @Override public boolean matches(final String input) { return true; }
    @Override public MatchType matchType() { return MatchType.EVERYTHING; }
  }

  private static final class LikeWithNoMatch implements LikePattern {
    private static final LikeWithNoMatch INSTANCE = new LikeWithNoMatch();
    @Override public boolean matches(final String input) { return false; }
    @Override public MatchType matchType() { return MatchType.NOTHING; }
  }

  private record LikeWithStringEquals(String expr) implements LikePattern {
    @Override
    public MatchType matchType() {
      return MatchType.FULL;
    }

    @Override
    public boolean matches(final String input) {
      return StringUtil.equals(input, expr);
    }
  }

  private record RegexLikePattern(Pattern pattern, MatchType matchType) implements LikePattern {
    private RegexLikePattern(final String pattern) {
      this(Pattern.compile(pattern), false);
    }

    private RegexLikePattern(final Pattern pattern, final boolean prefixMatch) {
      this(pattern, prefixMatch ? MatchType.PREFIX : MatchType.RANDOM);
    }

    @Override
    public MatchType matchType() {
      return matchType;
    }

    @Override
    public boolean matches(final String input) {
      return pattern.matcher(input).matches();
    }
  }
}
