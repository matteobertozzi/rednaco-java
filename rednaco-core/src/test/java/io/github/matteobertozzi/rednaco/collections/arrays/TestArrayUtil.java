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

public class TestArrayUtil {
  // ================================================================================
  //  length related
  // ================================================================================
  @Test
  public void testLength() {
    Assertions.assertEquals(0, ArrayUtil.length((boolean[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((short[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((int[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((long[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((float[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((double[]) null));
    Assertions.assertEquals(0, ArrayUtil.length((String[]) null));

    Assertions.assertEquals(0, ArrayUtil.length(new boolean[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new short[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new int[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new long[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new float[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new double[0]));
    Assertions.assertEquals(0, ArrayUtil.length(new String[0]));

    Assertions.assertEquals(3, ArrayUtil.length(new boolean[] { false, true, false }));
    Assertions.assertEquals(3, ArrayUtil.length(new short[] { 1, 2, 3 }));
    Assertions.assertEquals(3, ArrayUtil.length(new int[] { 1, 2, 3 }));
    Assertions.assertEquals(3, ArrayUtil.length(new long[] { 1, 2, 3 }));
    Assertions.assertEquals(3, ArrayUtil.length(new float[] { 1, 2, 3 }));
    Assertions.assertEquals(3, ArrayUtil.length(new double[] { 1, 2, 3 }));
    Assertions.assertEquals(3, ArrayUtil.length(new String[] { "a", "b", "c" }));
  }

  @Test
  public void testEmpty() {
    Assertions.assertTrue(ArrayUtil.isEmpty((boolean[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((short[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((int[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((long[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((float[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((double[]) null));
    Assertions.assertTrue(ArrayUtil.isEmpty((String[]) null));

    Assertions.assertTrue(ArrayUtil.isEmpty(new boolean[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new short[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new int[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new long[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new float[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new double[0]));
    Assertions.assertTrue(ArrayUtil.isEmpty(new String[0]));

    Assertions.assertFalse(ArrayUtil.isEmpty(new boolean[] { false, true, false }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new short[] { 1, 2, 3 }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new int[] { 1, 2, 3 }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new long[] { 1, 2, 3 }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new float[] { 1, 2, 3 }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new double[] { 1, 2, 3 }));
    Assertions.assertFalse(ArrayUtil.isEmpty(new String[] { "a", "b", "c" }));

    Assertions.assertFalse(ArrayUtil.isNotEmpty((boolean[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((short[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((int[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((long[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((float[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((double[]) null));
    Assertions.assertFalse(ArrayUtil.isNotEmpty((String[]) null));

    Assertions.assertFalse(ArrayUtil.isNotEmpty(new boolean[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new short[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new int[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new long[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new float[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new double[0]));
    Assertions.assertFalse(ArrayUtil.isNotEmpty(new String[0]));

    Assertions.assertTrue(ArrayUtil.isNotEmpty(new boolean[] { false, true, false }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new short[] { 1, 2, 3 }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new int[] { 1, 2, 3 }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new long[] { 1, 2, 3 }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new float[] { 1, 2, 3 }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new double[] { 1, 2, 3 }));
    Assertions.assertTrue(ArrayUtil.isNotEmpty(new String[] { "a", "b", "c" }));
  }

  // ================================================================================
  //  Array Item Swap related
  // ================================================================================
  @Test
  public void testSwap() {
    final boolean[] boolArray = new boolean[] { false, false, false, true, true };
    ArrayUtil.swap(boolArray, 1, 3);
    Assertions.assertArrayEquals(new boolean[] { false, true, false, false, true }, boolArray);

    final double[] doubleArray = new double[] { 1.1, 2.2, 3.3, 4.4, 5.5 };
    ArrayUtil.swap(doubleArray, 1, 3);
    Assertions.assertArrayEquals(new double[] { 1.1, 4.4, 3.3, 2.2, 5.5 }, doubleArray);

    final float[] floatArray = new float[] { 1.1f, 2.2f, 3.3f, 4.4f, 5.5f };
    ArrayUtil.swap(floatArray, 1, 3);
    Assertions.assertArrayEquals(new float[] { 1.1f, 4.4f, 3.3f, 2.2f, 5.5f }, floatArray);

    final long[] longArray = new long[] { 1, 2, 3, 4, 5 };
    ArrayUtil.swap(longArray, 1, 3);
    Assertions.assertArrayEquals(new long[] { 1, 4, 3, 2, 5 }, longArray);

    final int[] intArray = new int[] { 1, 2, 3, 4, 5 };
    ArrayUtil.swap(intArray, 1, 3);
    Assertions.assertArrayEquals(new int[] { 1, 4, 3, 2, 5 }, intArray);

    final short[] shortArray = new short[] { 1, 2, 3, 4, 5 };
    ArrayUtil.swap(shortArray, 1, 3);
    Assertions.assertArrayEquals(new short[] { 1, 4, 3, 2, 5 }, shortArray);

    final byte[] byteArray = new byte[] { 1, 2, 3, 4, 5 };
    ArrayUtil.swap(byteArray, 1, 3);
    Assertions.assertArrayEquals(new byte[] { 1, 4, 3, 2, 5 }, byteArray);

    final String[] strArray = new String[] { "aaa", "bbb", "ccc", "ddd", "eee" };
    ArrayUtil.swap(strArray, 1, 3);
    Assertions.assertArrayEquals(new String[] { "aaa", "ddd", "ccc", "bbb", "eee" }, strArray);
  }

  // ================================================================================
  //  Array toString() related
  // ================================================================================
  @Test
  public void testToString() {
    Assertions.assertEquals("null", ArrayUtil.toString((boolean[])null, 0, 0));
    Assertions.assertEquals("null", ArrayUtil.toString((short[])null, 0, 0));
    Assertions.assertEquals("null", ArrayUtil.toString((int[])null, 0, 0));
    Assertions.assertEquals("null", ArrayUtil.toString((long[])null, 0, 0));
    Assertions.assertEquals("null", ArrayUtil.toString((float[])null, 0, 0));
    Assertions.assertEquals("null", ArrayUtil.toString((double[])null, 0, 0));

    Assertions.assertEquals("[]", ArrayUtil.toString(new boolean[] { false, true, false, true }, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toString(new short[] { 1, 2, 3, 4 }, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toString(new int[] { 1, 2, 3, 4 }, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toString(new long[] { 1, 2, 3, 4 }, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toString(new float[] { 1.1f, 2.2f, 3.3f, 4.4f }, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toString(new double[] { 1.1, 2.2, 3.3, 4.4 }, 0, 0));

    Assertions.assertEquals("[false, true, false, true]", ArrayUtil.toString(new boolean[] { false, true, false, true }, 0, 4));
    Assertions.assertEquals("[1, 2, 3, 4]", ArrayUtil.toString(new short[] { 1, 2, 3, 4 }, 0, 4));
    Assertions.assertEquals("[1, 2, 3, 4]", ArrayUtil.toString(new int[] { 1, 2, 3, 4 }, 0, 4));
    Assertions.assertEquals("[1, 2, 3, 4]", ArrayUtil.toString(new long[] { 1, 2, 3, 4 }, 0, 4));
    Assertions.assertEquals("[1.1, 2.2, 3.3, 4.4]", ArrayUtil.toString(new float[] { 1.1f, 2.2f, 3.3f, 4.4f }, 0, 4));
    Assertions.assertEquals("[1.1, 2.2, 3.3, 4.4]", ArrayUtil.toString(new double[] { 1.1, 2.2, 3.3, 4.4 }, 0, 4));

    Assertions.assertEquals("[false, true]", ArrayUtil.toString(new boolean[] { false, true, false, true }, 2, 2));
    Assertions.assertEquals("[3, 4]", ArrayUtil.toString(new short[] { 1, 2, 3, 4 }, 2, 2));
    Assertions.assertEquals("[3, 4]", ArrayUtil.toString(new int[] { 1, 2, 3, 4 }, 2, 2));
    Assertions.assertEquals("[3, 4]", ArrayUtil.toString(new long[] { 1, 2, 3, 4 }, 2, 2));
    Assertions.assertEquals("[3.3, 4.4]", ArrayUtil.toString(new float[] { 1.1f, 2.2f, 3.3f, 4.4f }, 2, 2));
    Assertions.assertEquals("[3.3, 4.4]", ArrayUtil.toString(new double[] { 1.1, 2.2, 3.3, 4.4 }, 2, 2));

    Assertions.assertEquals("[true, false]", ArrayUtil.toString(new boolean[] { false, true, false, true }, 1, 2));
    Assertions.assertEquals("[2, 3]", ArrayUtil.toString(new short[] { 1, 2, 3, 4 }, 1, 2));
    Assertions.assertEquals("[2, 3]", ArrayUtil.toString(new int[] { 1, 2, 3, 4 }, 1, 2));
    Assertions.assertEquals("[2, 3]", ArrayUtil.toString(new long[] { 1, 2, 3, 4 }, 1, 2));
    Assertions.assertEquals("[2.2, 3.3]", ArrayUtil.toString(new float[] { 1.1f, 2.2f, 3.3f, 4.4f }, 1, 2));
    Assertions.assertEquals("[2.2, 3.3]", ArrayUtil.toString(new double[] { 1.1, 2.2, 3.3, 4.4 }, 1, 2));
  }

  @Test
  public void testToStringWithoutNulls() {
    final String[] testVec = new String[] { null, "aaa", null, null, "bbb", "ccc", null };
    Assertions.assertEquals("null", ArrayUtil.toStringWithoutNulls(null));
    Assertions.assertEquals("[]", ArrayUtil.toStringWithoutNulls(new String[0]));
    Assertions.assertEquals("[]", ArrayUtil.toStringWithoutNulls(testVec, 0, 0));
    Assertions.assertEquals("[]", ArrayUtil.toStringWithoutNulls(testVec, 0, 1));
    Assertions.assertEquals("[]", ArrayUtil.toStringWithoutNulls(testVec, 2, 2));
    Assertions.assertEquals("[aaa]", ArrayUtil.toStringWithoutNulls(testVec, 0, 2));
    Assertions.assertEquals("[aaa]", ArrayUtil.toStringWithoutNulls(testVec, 0, 3));
    Assertions.assertEquals("[aaa]", ArrayUtil.toStringWithoutNulls(testVec, 0, 4));
    Assertions.assertEquals("[aaa, bbb]", ArrayUtil.toStringWithoutNulls(testVec, 0, 5));
    Assertions.assertEquals("[aaa, bbb, ccc]", ArrayUtil.toStringWithoutNulls(testVec, 0, 6));
    Assertions.assertEquals("[aaa, bbb, ccc]", ArrayUtil.toStringWithoutNulls(testVec, 0, 7));
    Assertions.assertEquals("[bbb, ccc]", ArrayUtil.toStringWithoutNulls(testVec, 2, 5));
  }
}
