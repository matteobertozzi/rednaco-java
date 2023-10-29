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

package io.github.matteobertozzi.rednaco.collections.arrays;

import java.util.Objects;

public final class ArraySearchUtil {
  private ArraySearchUtil() {
    // no-op
  }

  @FunctionalInterface
  public interface ArrayIndexKeyComparator {
    int compareKeyWith(int index);
  }

  /**
   * @param a the array to be searched
   * @param off the offset to the first array element
   * @param len the array length to search into. last array element is (off + len)
   * @param stride the array stride
   * @param key the value to be searched for
   * @return index of the search key, if it is contained in the array;
   *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
   *         <i>insertion point</i> is defined as the point at which the
   *         key would be inserted into the array: the index of the first
   *         element greater than the key, or {@code len} if all
   *         elements in the array are less than the specified key. Note
   *         that this guarantees that the return value will be &gt;= 0 if
   *         and only if the key is found.
   */
  public static int binarySearch(final int[] a, final int off, final int len, final int stride, final int key) {
    int low = 0;
    int high = len - 1;

    while (low <= high) {
      final int mid = (low + high) >>> 1;
      final int midVal = a[off + (mid * stride)];

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    return -(low + 1);  // key not found.
  }

  public static int binarySearch(final int off, final int len, final ArrayIndexKeyComparator comparator) {
    int low = 0;
    int high = len - 1;

    while (low <= high) {
      final int mid = (low + high) >>> 1;
      final int cmp = comparator.compareKeyWith(mid);

      if (cmp > 0) {
        low = mid + 1;
      } else if (cmp < 0) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }

    return -(low + 1);  // key not found.
  }


  // ================================================================================
  //  PUBLIC Array indexOf helpers
  // ================================================================================
  public static int indexOf(final byte[] buf, final int off, final int len, final byte value) {
    for (int i = 0; i < len; ++i) {
      if (buf[off + i] == value) {
        return i;
      }
    }
    return -1;
  }

  public static int indexOf(final char[] buf, final int off, final int len, final char value) {
    for (int i = 0; i < len; ++i) {
      if (buf[off + i] == value) {
        return i;
      }
    }
    return -1;
  }

  public static int indexOf(final int[] buf, final int off, final int len, final int value) {
    for (int i = 0; i < len; ++i) {
      if (buf[off + i] == value) {
        return i;
      }
    }
    return -1;
  }

  public static int indexOf(final long[] buf, final int off, final int len, final long value) {
    for (int i = 0; i < len; ++i) {
      if (buf[off + i] == value) {
        return i;
      }
    }
    return -1;
  }

  public static int indexOf(final Object[] buf, final int off, final int len, final Object value) {
    for (int i = 0; i < len; ++i) {
      if (Objects.equals(buf[off + i], value)) {
        return i;
      }
    }
    return -1;
  }

  public static int indexOf(final byte[] buf, final byte value) {
    return buf == null ? -1 : indexOf(buf, 0, buf.length, value);
  }

  public static int indexOf(final char[] buf, final char value) {
    return buf == null ? -1 : indexOf(buf, 0, buf.length, value);
  }

  public static int indexOf(final int[] buf, final int value) {
    return buf == null ? -1 : indexOf(buf, 0, buf.length, value);
  }

  public static int indexOf(final long[] buf, final long value) {
    return buf == null ? -1 : indexOf(buf, 0, buf.length, value);
  }

  public static <T> int indexOf(final T[] items, final T value) {
    for (int i = 0; i < items.length; ++i) {
      if (Objects.equals(items[i], value)) {
        return i;
      }
    }
    return -1;
  }

  public static <T> boolean contains(final int[] items, final int value) {
    return items != null && indexOf(items, value) >= 0;
  }

  public static <T> boolean contains(final long[] items, final long value) {
    return items != null && indexOf(items, value) >= 0;
  }

  public static <T> boolean contains(final T[] items, final T value) {
    return items != null && indexOf(items, value) >= 0;
  }
}
