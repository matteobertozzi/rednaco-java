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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import io.github.matteobertozzi.rednaco.strings.StringBuilderUtil;

public final class ArrayUtil {
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  private ArrayUtil() {
    // no-op
  }

  // ================================================================================
  //  PUBLIC unchecked cast related
  // ================================================================================
  @SuppressWarnings("unchecked")
  public static <T> T getItemAt(final Object[] array, final int index) {
    return (T) array[index];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] newArray(final int size, final Class<?> clazz) {
    return (T[]) Array.newInstance(clazz, size);
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] newArray(final int size) {
    return (T[]) new Object[size];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] emptyArray() {
    return (T[]) EMPTY_OBJECT_ARRAY;
  }

  // ================================================================================
  //  PUBLIC Array length related
  // ================================================================================
  public static int length(final byte[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final char[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final boolean[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final short[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final int[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final long[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final float[] input) {
    return input == null ? 0 : input.length;
  }

  public static int length(final double[] input) {
    return input == null ? 0 : input.length;
  }

  public static <T> int length(final T[] input) {
    return input == null ? 0 : input.length;
  }

  public static boolean isEmpty(final byte[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final char[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final boolean[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final short[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final int[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final long[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final float[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final double[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isEmpty(final Object[] input) {
    return (input == null) || (input.length == 0);
  }

  public static boolean isNotEmpty(final byte[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final char[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final boolean[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final short[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final int[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final float[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final double[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final long[] input) {
    return (input != null) && (input.length != 0);
  }

  public static boolean isNotEmpty(final Object[] input) {
    return (input != null) && (input.length != 0);
  }

  // ================================================================================
  //  New Array from array related - map()
  // ================================================================================
  public static <T> T[] newArrayFrom(final Supplier<T>[] suppliers) {
    final T[] newData = newArray(suppliers.length);
    for (int i = 0; i < suppliers.length; ++i) {
      newData[i] = suppliers[i].get();
    }
    return newData;
  }

  public static <T> int[] newIntArrayFrom(final T[] values, final ToIntFunction<T> mapper) {
    final int[] newData = new int[values.length];
    for (int i = 0; i < values.length; ++i) {
      newData[i] = mapper.applyAsInt(values[i]);
    }
    return newData;
  }

  public static <T> long[] newLongArrayFrom(final T[] values, final ToLongFunction<T> mapper) {
    final long[] newData = new long[values.length];
    for (int i = 0; i < values.length; ++i) {
      newData[i] = mapper.applyAsLong(values[i]);
    }
    return newData;
  }

  public static <T> double[] newDoubleArrayFrom(final T[] values, final ToDoubleFunction<T> mapper) {
    final double[] newData = new double[values.length];
    for (int i = 0; i < values.length; ++i) {
      newData[i] = mapper.applyAsDouble(values[i]);
    }
    return newData;
  }

  public static <TIn, TOut> TOut[] newArrayFrom(final TIn[] values, final Function<TIn, TOut> mapper) {
    final TOut[] newData = newArray(values.length);
    for (int i = 0; i < values.length; ++i) {
      newData[i] = mapper.apply(values[i]);
    }
    return newData;
  }

  // ================================================================================
  //  New Array from list related - map()
  // ================================================================================
  public static <T> int[] newIntArrayFrom(final Collection<T> values, final ToIntFunction<T> mapper) {
    int index = 0;
    final int[] newData = new int[values.size()];
    for (final T value: values) {
      newData[index++] = mapper.applyAsInt(value);
    }
    return newData;
  }

  public static <T> long[] newLongArrayFrom(final Collection<T> values, final ToLongFunction<T> mapper) {
    int index = 0;
    final long[] newData = new long[values.size()];
    for (final T value: values) {
      newData[index++] = mapper.applyAsLong(value);
    }
    return newData;
  }

  public static <T> double[] newDoubleArrayFrom(final Collection<T> values, final ToDoubleFunction<T> mapper) {
    int index = 0;
    final double[] newData = new double[values.size()];
    for (final T value: values) {
      newData[index++] = mapper.applyAsDouble(value);
    }
    return newData;
  }

  public static <TIn, TOut> TOut[] newArrayFrom(final Collection<TIn> values, final Function<TIn, TOut> mapper) {
    int index = 0;
    final TOut[] newData = newArray(values.size());
    for (final TIn value: values) {
      newData[index++] = mapper.apply(value);
    }
    return newData;
  }

  // ================================================================================
  //  PUBLIC Array Consumer Interfaces
  // ================================================================================
  public interface ArrayConsumer<T> {
    void accept(T[] buf, int off, int len);
  }

  public interface ShortArrayConsumer {
    void accept(short[] buf, int off, int len);
  }

  public interface IntArrayConsumer {
    void accept(int[] buf, int off, int len);
  }

  public interface LongArrayConsumer {
    void accept(long[] buf, int off, int len);
  }

  public interface FloatArrayConsumer {
    void accept(float[] buf, int off, int len);
  }

  public interface DoubleArrayConsumer {
    void accept(double[] buf, int off, int len);
  }

  // ================================================================================
  //  PUBLIC Array resize helpers
  // ================================================================================
  public static byte[] newIfNotAtSize(final byte[] buf, final int size) {
    if (buf != null && buf.length >= size) return buf;
    return new byte[size];
  }

  public static int[] newIfNotAtSize(final int[] buf, final int size) {
    if (buf != null && buf.length >= size) return buf;
    return new int[size];
  }

  public static long[] newIfNotAtSize(final long[] buf, final int size) {
    if (buf != null && buf.length >= size) return buf;
    return new long[size];
  }

  // ================================================================================
  //  PUBLIC Array copy helpers
  // ================================================================================
  public static int[] copy(final int[] other) {
    return Arrays.copyOf(other, length(other));
  }

  public static long[] copy(final long[] other) {
    return Arrays.copyOf(other, length(other));
  }

  public static <T> T[] copy(final T[] other) {
    return Arrays.copyOf(other, length(other));
  }

  public static byte[] copyIfNotAtSize(final byte[] buf, final int off, final int len) {
    if (off == 0 && buf.length == len) return buf;
    return Arrays.copyOfRange(buf, off, off + len);
  }

  public static int[] copyIfNotAtSize(final int[] buf, final int off, final int len) {
    if (off == 0 && buf.length == len) return buf;
    return Arrays.copyOfRange(buf, off, off + len);
  }

  public static long[] copyIfNotAtSize(final long[] buf, final int off, final int len) {
    if (off == 0 && buf.length == len) return buf;
    return Arrays.copyOfRange(buf, off, off + len);
  }

  public static <T> T[] copyIfNotAtSize(final T[] buf, final int off, final int len) {
    if (off == 0 && buf.length == len) return buf;
    return Arrays.copyOfRange(buf, off, off + len);
  }

  public static <T> int countNotNull(final T[] data) {
    if (data == null) return 0;

    int notNull = 0;
    for (int i = 0; i < data.length; ++i) {
      notNull += (data[i] != null) ? 1 : 0;
    }
    return notNull;
  }

  public static <T> T[] copyNotNull(final Object[] src) {
    return copyNotNull(src, countNotNull(src));
  }

  public static <T> T[] copyNotNull(final Object[] src, final int notNullCount) {
    if (src == null) return null;
    return copyNotNull(src.getClass().getComponentType(), src, notNullCount);
  }

  public static <T> T[] copyNotNull(final Class<?> clazz, final Object[] src, final int notNullCount) {
    if (src == null) return null;

    final Class<?> itemType = (clazz == Object.class) ? getElementType(src, clazz) : clazz;
    final T[] notNull = newArray(notNullCount, itemType);
    for (int i = 0, count = 0; i < src.length; ++i) {
      if (src[i] != null) {
        notNull[count++] = getItemAt(src, i);
      }
    }
    return notNull;
  }

  private static Class<?> getElementType(final Object[] src, final Class<?> defaultType) {
    for (int i = 0; i < src.length; ++i) {
      if (src[i] != null) {
        return src[i].getClass();
      }
    }
    return defaultType;
  }

  // ================================================================================
  //  PUBLIC Array copy helpers
  // ================================================================================
  public static void copy(final long[] dstArray, final int dstIndex, final long[] srcArray, final int srcFromIndex, final int srcToIndex) {
    for (int i = 0, len = srcToIndex - srcFromIndex; i < len; ++i) {
      dstArray[dstIndex + i] = srcArray[srcFromIndex + i];
    }
  }

  public static void copyStride(final long[] dstArray, final int dstIndex, final long[] srcArray, final int srcIndex, final int length, final int stride) {
    int index = 0;
    for (int i = 0; i < length; ++i) {
      dstArray[dstIndex + i] = srcArray[srcIndex + index];
      index += stride;
    }
  }

  // ================================================================================
  //  PUBLIC Array insert helpers
  // ================================================================================
  public static void insert(final int[] array, final int off, final int len,
      final int index, final int value) {
    System.arraycopy(array, off + index, array, off + index + 1, len - index);
    array[index] = value;
  }

  public static void insert(final long[] array, final int off, final int len,
      final int index, final long value) {
    System.arraycopy(array, off + index, array, off + index + 1, len - index);
    array[index] = value;
  }

  public static <T> void insert(final T[] array, final int off, final int len,
      final int index, final T value) {
    System.arraycopy(array, off + index, array, off + index + 1, len - index);
    array[index] = value;
  }

  // ================================================================================
  //  PUBLIC Array Remove related
  // ================================================================================
  public static void removeElementWithShift(final int[] arr, final int index){
    System.arraycopy(arr, index + 1, arr, index, arr.length - (1 + index));
  }

  public static void removeElementWithShift(final long[] arr, final int index){
    System.arraycopy(arr, index + 1, arr, index, arr.length - (1 + index));
  }

  public static <T> void removeElementWithShift(final T[] arr, final int index){
    System.arraycopy(arr, index + 1, arr, index, arr.length - (1 + index));
  }

  public static <T> T[] subarray(final T[] array, int startIndexInclusive, int endIndexExclusive) {
    if (array == null) return null;

    if (startIndexInclusive < 0) startIndexInclusive = 0;
    if (endIndexExclusive > array.length) endIndexExclusive = array.length;

    if (startIndexInclusive == 0 && endIndexExclusive == array.length) {
      return array;
    }

    final int newSize = endIndexExclusive - startIndexInclusive;
    final Class<?> type = array.getClass().getComponentType();
    if (newSize <= 0) {
      final T[] subarray = newArray(0, type);
      return subarray;
    }

    final T[] subarray = newArray(newSize, type);
    System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
    return subarray;
  }

  public static int[] subarray(final int[] array, int startIndexInclusive, int endIndexExclusive) {
    if (array == null) return null;

    if (startIndexInclusive < 0) startIndexInclusive = 0;
    if (endIndexExclusive > array.length) endIndexExclusive = array.length;

    if (startIndexInclusive == 0 && endIndexExclusive == array.length) {
      return array;
    }

    final int newSize = endIndexExclusive - startIndexInclusive;
    if (newSize <= 0) return new int[0];

    final int[] subarray = new int[newSize];
    System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
    return subarray;
  }

  // ================================================================================
  //  PUBLIC Array Clear related
  // ================================================================================
  public static void clear(final byte[] data) {
    if (isEmpty(data)) return;
    Arrays.fill(data, Byte.MAX_VALUE);
  }

  public static void clear(final char[] data) {
    if (isEmpty(data)) return;
    Arrays.fill(data, Character.MAX_VALUE);
  }

  public static void clear(final int[] data) {
    if (isEmpty(data)) return;
    Arrays.fill(data, Integer.MAX_VALUE);
  }

  public static void clear(final long[] data) {
    if (isEmpty(data)) return;
    Arrays.fill(data, Long.MAX_VALUE);
  }

  public static void clear(final Object[] data) {
    if (isEmpty(data)) return;
    Arrays.fill(data, null);
  }

  // ================================================================================
  //  PUBLIC Array Item Swap related
  // ================================================================================
  public static void swap(final boolean[] values, final int aIndex, final int bIndex) {
    final boolean  tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final byte[] values, final int aIndex, final int bIndex) {
    final byte tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final short[] values, final int aIndex, final int bIndex) {
    final short tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final int[] values, final int aIndex, final int bIndex) {
    final int tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final long[] values, final int aIndex, final int bIndex) {
    final long tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final float[] values, final int aIndex, final int bIndex) {
    final float tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static void swap(final double[] values, final int aIndex, final int bIndex) {
    final double tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  public static <T> void swap(final T[] values, final int aIndex, final int bIndex) {
    final T tmp = values[aIndex];
    values[aIndex] = values[bIndex];
    values[bIndex] = tmp;
  }

  // ================================================================================
  //  PUBLIC Array concat() helpers
  // ================================================================================
  public static String[] concat(final String[]... arrays) {
    int length = 0;
    for (int i = 0; i < arrays.length; ++i) {
      length += length(arrays[i]);
    }

    int index = 0;
    final String[] values = new String[length];
    for (int i = 0; i < arrays.length; ++i) {
      final int len = length(arrays[i]);
      if (len == 0) continue;

      System.arraycopy(arrays[i], 0, values, index, len);
      index += len;
    }
    return values;
  }

  @SafeVarargs
  public static <T> T[] addAll(final T[] array1, final T... array2) {
    if (array1 == null) return clone(array2);
    if (array2 == null) return clone(array1);

    final Class<?> type1 = array1.getClass().getComponentType();
    final T[] joinedArray = newArray(array1.length + array2.length, type1);
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);

    try {
      System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
      return joinedArray;
    } catch (final ArrayStoreException e) {
      final Class<?> type2 = array2.getClass().getComponentType();
      if (!type1.isAssignableFrom(type2)) {
        throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), e);
      }
      throw e;
    }
  }

  public static <T> T[] clone(final T[] array) {
    return array == null ? null : array.clone();
  }

  // ================================================================================
  //  PUBLIC Array sum helpers
  // ================================================================================
  public static long sum(final int[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    long sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static long sum(final long[] buf, final int off, final int len) {
    if (buf == null || len == 0) return 0;

    long sum = 0;
    for (int i = 0; i < len; ++i) {
      sum += buf[off + i];
    }
    return sum;
  }

  public static int max(final int[] buf, final int off, final int len) {
    int maxValue = buf[off];
    for (int i = 1; i < len; ++i) {
      maxValue = Math.max(maxValue, buf[off + i]);
    }
    return maxValue;
  }

  // ================================================================================
  //  PUBLIC Array toString() helpers
  // ================================================================================
  public static String toString(final boolean[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final short[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final int[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final long[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final float[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final double[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toString(final Object[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";
    return StringBuilderUtil.appendArray(new StringBuilder(len * 5), a, off, len).toString();
  }

  public static String toStringWithoutNulls(final Object[] a) {
    if (a == null) return "null";
    if (a.length == 0) return "[]";
    return toStringWithoutNulls(a, 0, a.length);
  }

  public static String toStringWithoutNulls(final Object[] a, final int off, final int len) {
    if (a == null) return "null";
    if (len == 0) return "[]";

    final StringBuilder b = new StringBuilder(a.length * 5);
    b.append('[');
    int count = 0;
    for (int i = 0; i < len; ++i) {
      final Object v = a[off + i];
      if (v == null) continue;

      if (count++ > 0) b.append(", ");
      b.append(v);
    }
    return b.append(']').toString();
  }
}
