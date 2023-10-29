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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.matteobertozzi.rednaco.strings.StringBuilderUtil.ValueAppenderConverter;

public final class StringFormat {
  private static final Pattern POSITIONAL_PATTERN = Pattern.compile("\\{(([0-9]+)(\\:[-_a-zA-Z0-9]+)*)}");
  private static final Pattern KEYWORD_PATTERN = Pattern.compile("\\{([-_a-zA-Z0-9]*)}");
  private static final String UNPROVIDED_ARG_LABEL = "{UNPROVIDED_ARG}";

  private StringFormat() {
    // no-op
  }

  // ========================================================================================================================
  //  Named Format: "foo {} bar {name}..."
  // ========================================================================================================================
  public static String namedFormat(final String format, final Object... args) {
    final StringBuilder builder = new StringBuilder(format.length() + (args.length * 8));
    applyNamedFormat(builder, format, args);
    return builder.toString();
  }

  public static int applyNamedFormat(final StringBuilder msgBuilder, final String format, final Object[] args) {
    return applyNamedFormat(msgBuilder, format, args, 0, args != null ? args.length : 0, StringBuilderUtil::appendValue);
  }

  public static int applyNamedFormat(final StringBuilder msgBuilder, final String format,
      final Object[] args, final int argsOff, final int argsLen,
      final ValueAppenderConverter valueAppender) {
    if (argsLen == 0) {
      msgBuilder.append(format);
      return 0;
    }

    int formatOffset = 0;
    int argsIndex = argsOff;
    final Matcher m = KEYWORD_PATTERN.matcher(format);
    while (m.find()) {
      final int keyStart = m.start(1);
      final int keyEnd = m.end(1);

      msgBuilder.append(format, formatOffset, keyStart - 1);
      if ((keyEnd - keyStart) != 0) {
        msgBuilder.append(format, keyStart, keyEnd).append(':');
      }

      if (argsIndex < argsLen) {
        valueAppender.append(msgBuilder, args[argsOff + argsIndex++]);
      } else {
        msgBuilder.append(UNPROVIDED_ARG_LABEL);
      }

      formatOffset = keyEnd + 1;
    }
    msgBuilder.append(format, formatOffset, format.length());
    return argsIndex;
  }

  // ====================================================================================================
  //  Positional Format: "foo {0} bar {1:translator-notes}..."
  // ====================================================================================================
  public static String positionalFormat(final String format, final Object... args) {
    final StringBuilder buf = new StringBuilder(format.length() + (args.length * 8));
    applyPositionalFormat(buf, format, args, 0, args.length, StringBuilderUtil::appendValue);
    return buf.toString();
  }

  public static void applyPositionalFormat(final StringBuilder msgBuilder, final String format, final Object[] args) {
    applyPositionalFormat(msgBuilder, format, args, 0, args != null ? args.length : 0, StringBuilderUtil::appendValue);
  }

  public static void applyPositionalFormat(final StringBuilder msgBuilder, final String format,
      final Object[] args, final int argsOff, final int argsLen,
      final ValueAppenderConverter valueAppender) {
    if (argsLen == 0) {
      msgBuilder.append(format);
      return;
    }

    int formatOffset = 0;
    final Matcher m = POSITIONAL_PATTERN.matcher(format);
    while (m.find()) {
      final int keyStart = m.start(2);
      final int keyEnd = m.end(2);
      final int argIndex = Integer.parseInt(format, keyStart, keyEnd, 10);

      msgBuilder.append(format, formatOffset, keyStart - 1);
      if (argIndex < argsLen) {
        valueAppender.append(msgBuilder, args[argsOff + argIndex]);
      } else {
        msgBuilder.append(UNPROVIDED_ARG_LABEL);
      }

      formatOffset = m.end(0);
    }
    msgBuilder.append(format, formatOffset, format.length());
  }

  // ====================================================================================================
  //  Value Of
  // ====================================================================================================
  public static String valueOf(final Object value) {
    return StringBuilderUtil.appendValue(new StringBuilder(), value).toString();
  }
}
