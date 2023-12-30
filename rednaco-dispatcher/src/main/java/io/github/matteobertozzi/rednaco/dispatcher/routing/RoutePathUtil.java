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

package io.github.matteobertozzi.rednaco.dispatcher.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.matteobertozzi.rednaco.bytes.ByteArray;
import io.github.matteobertozzi.rednaco.collections.arrays.IntArray;

public final class RoutePathUtil {
  public record RouterPathSpec(byte[] path, Pattern pattern) {
    public Matcher match(final String path) {
      return pattern.matcher(path);
    }
  }

  private RoutePathUtil() {
    // no-op
  }

  public static RouterPathSpec parsePath(final String rawPath) {
    final String path = cleanPath(rawPath);
    if (path.indexOf('{') >= 0 || path.indexOf('(') >= 0) {
      throw new UnsupportedOperationException("@UriMapping does not work for path with variables or patterns. use @UriVariableMapping or @UriPatternMapping for " + rawPath);
    }

    final byte[] pathBytes = new byte[path.length() + 1];
    for (int i = 0, n = pathBytes.length - 1; i < n; ++i) {
      pathBytes[i] = (byte)(path.charAt(i) & 0xff);
    }
    return new RouterPathSpec(pathBytes, null);
  }

  private static final Pattern URI_VARIABLE_MAPPING_PATTERN = Pattern.compile("\\{(.*?)\\}");
  public static RouterPathSpec parsePathWithVariables(final String rawPath) {
    final String path = cleanPath(rawPath);
    if (path.indexOf('{') < 0) {
      if (path.indexOf('(') >= 0) {
        throw new UnsupportedOperationException("No {variables} found, use @UriPatternMapping if you are using regex. or @UriMapping for direct mapping: " + rawPath);
      }
      throw new UnsupportedOperationException("@UriVariableMapping is slow, do not use it if not needed. Use @UriMapping for " + rawPath);
    }

    final Matcher m = URI_VARIABLE_MAPPING_PATTERN.matcher(path);
    final StringBuilder pathPattern = new StringBuilder(path.length() + 32);
    final ByteArray pathBytes = new ByteArray(path.length());
    int offset = 0;
    pathPattern.append('^');
    while (m.find()) {
      final String groupName = m.group(1);

      final int keyStart = m.start(0);
      while (offset < keyStart) {
        pathBytes.add(path.charAt(offset++));
      }
      pathBytes.add('*');

      m.appendReplacement(pathPattern, "(?<" + groupName + ">[^/]*)");
      offset = m.end(0);
    }
    m.appendTail(pathPattern);
    pathPattern.append('$');
    while (offset < path.length()) {
      pathBytes.add(path.charAt(offset++));
    }
    pathBytes.add(0);

    return new RouterPathSpec(pathBytes.drain(), Pattern.compile(pathPattern.toString()));
  }

  private static final Pattern URI_REGEX_MAPPING_PATTERN = Pattern.compile("\\((.*?)\\)");
  public static RouterPathSpec parsePathWithPattern(final String rawPath) {
    final String path = cleanPath(rawPath);
    if (path.indexOf('(') < 0) {
      if (path.indexOf('{') >= 0) {
        throw new UnsupportedOperationException("No regex group found, use @UriVariableMapping if you are using {variables}. or @UriMapping for direct mapping: " + rawPath);
      }
      throw new UnsupportedOperationException("@UriPatternMapping is slow, do not use it if not needed. Use @UriMapping for " + rawPath);
    }

    final Matcher m = URI_REGEX_MAPPING_PATTERN.matcher(path);
    final ByteArray pathBytes = new ByteArray(path.length());
    int offset = 0;
    while (m.find()) {
      final int keyStart = m.start(0);
      while (offset < keyStart) {
        pathBytes.add(path.charAt(offset++));
      }
      pathBytes.add('*');
      offset = m.end(0);
    }
    while (offset < path.length()) {
      pathBytes.add(path.charAt(offset++));
    }
    pathBytes.add(0);

    return new RouterPathSpec(pathBytes.drain(), Pattern.compile('^' + path + '$'));
  }

  public static String cleanPath(final String path) {
    if (path.charAt(0) != '/') {
      throw new IllegalArgumentException("expected a path starting with /");
    }

    final IntArray parts = new IntArray(32);
    final int pathLen = path.length();
    int resultSize = 0;

    for (int r = 1; r < pathLen;) {
      final char c = path.charAt(r);

      if (c == '/') {
        // empty path element
        r++;
      } else if (c == '.' && (r + 1 == pathLen || path.charAt(r + 1) == '/')) {
        // . element
        r++;
      } else if (c == '.' && path.charAt(r + 1) == '.' && (r + 2 == pathLen || path.charAt(r + 2) == '/')) {
        r += 2;

        if (parts.isEmpty()) {
          throw new IllegalArgumentException("invalid path, trying to go back too much");
        }

        final int length = parts.removeLast();
        parts.removeLast();
        resultSize -= length;
      } else {
        final int startOffset = r;
        int length = 0;
        for (; r < pathLen && path.charAt(r) != '/'; ++r) {
          length++;
        }
        parts.add(startOffset);
        parts.add(length);
        resultSize += length;
      }
    }

    if (parts.isEmpty()) {
      return "/";
    }

    final StringBuilder builder = new StringBuilder(resultSize + (parts.size() / 2));
    for (int i = 0, n = parts.size(); i < n; i += 2) {
      final int startOffset = parts.get(i);
      final int length = parts.get(i + 1);
      builder.append('/');
      builder.append(path, startOffset, startOffset + length);
    }
    return builder.toString();
  }


  private static boolean isValid(final int c) {
    // ABCDEFGHIJKLMNOPQRSTUVWXYZ
    // abcdefghijklmnopqrstuvwxyz
    // 0123456789
    // -._~:/?#[]@!$&'()*+,;=
    return (c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)
        || (c == '.' || c == '-' || c == '_');
  }
}
