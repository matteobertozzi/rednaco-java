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

public final class ArraySortUtil {
  private ArraySortUtil() {
    // no-op
  }

  // ================================================================================
  //  Is Sorted related
  // ================================================================================
  public static boolean isSorted(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return true;

    int lastValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (lastValue > buf[off + i]) {
        return false;
      }
      lastValue = buf[off + i];
    }
    return true;
  }

  public static boolean isSorted(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return true;

    long lastValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (lastValue > buf[off + i]) {
        return false;
      }
      lastValue = buf[off + i];
    }
    return true;
  }

  public static boolean isSorted(final float[] buf, final int off, final int len) {
    if (buf == null || len == 0) return true;

    float lastValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (lastValue > buf[off + i]) {
        return false;
      }
      lastValue = buf[off + i];
    }
    return true;
  }

  public static boolean isSorted(final double[] buf, final int off, final int len) {
    if (buf == null || len == 0) return true;

    double lastValue = buf[off];
    for (int i = 1; i < len; ++i) {
      if (lastValue > buf[off + i]) {
        return false;
      }
      lastValue = buf[off + i];
    }
    return true;
  }

  // ================================================================================
  //  Sort related
  // ================================================================================

  @FunctionalInterface
  public interface ArrayIndexComparator {
    int compare(int aIndex, int bIndex);
  }

  @FunctionalInterface
  public interface ArrayIndexSwapper {
    void swap(int aIndex, int bIndex);
  }

  public static void sort(final int off, final int len,
      final ArrayIndexComparator comparator, final ArrayIndexSwapper swapper) {
    int i = (len / 2 - 1);

    // heapify
    for (; i >= 0; --i) {
      int c = i * 2 + 1;
      int r = i;
      while (c < len) {
        if (c < len - 1 && comparator.compare(off + c, off + c + 1) < 0) {
          c += 1;
        }
        if (comparator.compare(off + r, off + c) >= 0) {
          break;
        }
        swapper.swap(off + r, off + c);
        r = c;
        c = r * 2 + 1;
      }
    }

    // sort
    for (i = len - 1; i > 0; --i) {
      int c = 1;
      int r = 0;
      swapper.swap(off, off + i);
      while (c < i) {
        if (c < i - 1 && comparator.compare(off + c, off + c + 1) < 0) {
          c += 1;
        }
        if (comparator.compare(off + r, off + c) >= 0) {
          break;
        }
        swapper.swap(off + r, off + c);
        r = c;
        c = r * 2 + 1;
      }
    }
  }
}
