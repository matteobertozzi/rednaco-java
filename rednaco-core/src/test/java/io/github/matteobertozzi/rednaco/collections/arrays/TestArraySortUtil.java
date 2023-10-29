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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestArraySortUtil {
  @Test
  public void testIsSorted() {
    Assertions.assertTrue(ArraySortUtil.isSorted(new int[0], 0, 0));
    Assertions.assertTrue(ArraySortUtil.isSorted(new int[] { 1, 2, 3 }, 0, 0));

    Assertions.assertTrue(ArraySortUtil.isSorted(new int[] { 1, 2, 3 }, 0, 1));
    Assertions.assertTrue(ArraySortUtil.isSorted(new int[] { 1, 2, 3 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new int[] { 1, 2, 2 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new int[] { 3, 2, 4 }, 1, 2));
    Assertions.assertFalse(ArraySortUtil.isSorted(new int[] { 3, 2, 4 }, 0, 3));
    Assertions.assertFalse(ArraySortUtil.isSorted(new int[] { 3, 2, 1 }, 0, 3));

    Assertions.assertTrue(ArraySortUtil.isSorted(new long[] { 1, 2, 3 }, 0, 1));
    Assertions.assertTrue(ArraySortUtil.isSorted(new long[] { 1, 2, 3 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new long[] { 1, 2, 2 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new long[] { 3, 2, 4 }, 1, 2));
    Assertions.assertFalse(ArraySortUtil.isSorted(new long[] { 3, 2, 4 }, 0, 3));
    Assertions.assertFalse(ArraySortUtil.isSorted(new long[] { 3, 2, 1 }, 0, 3));

    Assertions.assertTrue(ArraySortUtil.isSorted(new float[] { 1.1f, 2.2f, 3.3f }, 0, 1));
    Assertions.assertTrue(ArraySortUtil.isSorted(new float[] { 1.1f, 2.2f, 3.3f }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new float[] { 1.1f, 2.2f, 2.2f }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new float[] { 3.3f, 2.2f, 4.4f }, 1, 2));
    Assertions.assertFalse(ArraySortUtil.isSorted(new float[] { 3.3f, 2.2f, 4.4f }, 0, 3));
    Assertions.assertFalse(ArraySortUtil.isSorted(new float[] { 3.3f, 2.2f, 1.1f }, 0, 3));

    Assertions.assertTrue(ArraySortUtil.isSorted(new double[] { 1.1, 2.2, 3.3 }, 0, 1));
    Assertions.assertTrue(ArraySortUtil.isSorted(new double[] { 1.1, 2.2, 3.3 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new double[] { 1.1, 2.2, 2.2 }, 0, 3));
    Assertions.assertTrue(ArraySortUtil.isSorted(new double[] { 3.3, 2.2, 4.4 }, 1, 2));
    Assertions.assertFalse(ArraySortUtil.isSorted(new double[] { 3.3, 2.2, 4.4 }, 0, 3));
    Assertions.assertFalse(ArraySortUtil.isSorted(new double[] { 3.3, 2.2, 1.1 }, 0, 3));
  }

  @Test
  public void testSortStride() {
    final int[] index = new int[] { 1, 10, 2, 20, 3, 30, 4, 40 };
    ArraySortUtil.sort(0, index.length / 2,
      (a, b) -> Long.compare(index[b * 2], index[a * 2]),
      (a, b) -> {
        final int aIndex = a * 2;
        final int bIndex = b * 2;
        ArrayUtil.swap(index, aIndex, bIndex);
        ArrayUtil.swap(index, aIndex + 1, bIndex + 1);
      });
    Assertions.assertArrayEquals(new int[] { 4, 40, 3, 30, 2, 20, 1, 10 }, index);
  }

  @Test
  public void testSortCorrelatedArrays() {
    final int[] keys = { 5,  1,  3,  2  };
    final int[] col1 = { 50, 10, 30, 20 };
    final int[] col2 = { 51, 11, 33, 22 };

    ArraySortUtil.sort(0, keys.length,
      (a, b) -> Integer.compare(keys[a], keys[b]), // compare the keys
      (a, b) -> { // swap the items from all 3 arrays
        ArrayUtil.swap(keys, a, b);
        ArrayUtil.swap(col1, a, b);
        ArrayUtil.swap(col2, a, b);
      }
    );
    Assertions.assertArrayEquals(new int[] {  1,  2,  3,  5 }, keys);
    Assertions.assertArrayEquals(new int[] { 10, 20, 30, 50 }, col1);
    Assertions.assertArrayEquals(new int[] { 11, 22, 33, 51 }, col2);
  }
}
