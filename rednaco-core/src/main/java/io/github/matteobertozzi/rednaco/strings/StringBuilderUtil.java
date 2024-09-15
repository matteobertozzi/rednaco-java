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

import java.util.Map;

public final class StringBuilderUtil {
  private StringBuilderUtil() {
    // no-op
  }

  // ====================================================================================================
  //  Value Appender Converter
  // ====================================================================================================
  @FunctionalInterface
  public interface ValueAppenderConverter {
    void append(StringBuilder builder, Object value);
  }

  // ====================================================================================================
  //  Append Value
  // ====================================================================================================
  public static StringBuilder appendValue(final StringBuilder builder, final Object value) {
    if (value == null) return builder.append("null");

    final Class<?> clazz = value.getClass();
    if (clazz.isPrimitive()) {
      return builder.append(value);
    }

    if (clazz.isArray()) {
      return switch (value) {
        case final byte[] bArray -> appendArray(builder, bArray);
        case final int[] iArray -> appendArray(builder, iArray);
        case final long[] lArray -> appendArray(builder, lArray);
        case final short[] sArray -> appendArray(builder, sArray);
        case final float[] fArray -> appendArray(builder, fArray);
        case final double[] dArray -> appendArray(builder, dArray);
        case final boolean[] bArray -> appendArray(builder, bArray);
        case final Object[] oArray -> appendArray(builder, oArray);
        default -> builder.append(value);
      };
    } else if (value instanceof final Map<?, ?> map) {
      return appendMap(builder, map);
    }
    return builder.append(value);
  }

  // ====================================================================================================
  //  Append Array
  // ====================================================================================================
  public static StringBuilder appendArray(final StringBuilder builder, final boolean[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final boolean[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final byte[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final byte[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final short[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final short[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final int[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final int[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final long[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final long[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final float[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final float[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final double[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final double[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  public static StringBuilder appendArray(final StringBuilder builder, final Object[] a) {
    if (a == null) return builder.append("null");
    return appendArray(builder, a, 0, a.length);
  }

  public static StringBuilder appendArray(final StringBuilder builder, final Object[] a, final int off, final int len) {
    if (a == null) return builder.append("null");
    if (len == 0) return builder.append("[]");

    builder.append('[');
    builder.append(a[off]);
    for (int i = 1; i < len; ++i) {
      builder.append(", ").append(a[off + i]);
    }
    return builder.append(']');
  }

  // ====================================================================================================
  //  Append Map
  // ====================================================================================================
  public static StringBuilder appendMap(final StringBuilder builder, final Map<?, ?> map) {
    builder.append('{');
    int index = 0;
    for (final Map.Entry<?, ?> entry: map.entrySet()) {
      if (index++ > 0) builder.append(", ");
      appendValue(builder, entry.getKey());
      builder.append(':');
      appendValue(builder, entry.getValue());
    }
    return builder.append('}');
  }
}
