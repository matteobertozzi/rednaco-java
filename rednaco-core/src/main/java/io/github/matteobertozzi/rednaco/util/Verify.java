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

package io.github.matteobertozzi.rednaco.util;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.lists.ListUtil;
import io.github.matteobertozzi.rednaco.collections.maps.MapUtil;
import io.github.matteobertozzi.rednaco.localization.LocalizedResource;
import io.github.matteobertozzi.rednaco.localization.LocalizedText;
import io.github.matteobertozzi.rednaco.strings.StringUtil;

public class Verify {
  private static final LocalizedResource LOCALIZED_INVALID_ARGS = new LocalizedResource("verify.arg.generic.invalid.args", "invalid args");
  private static final LocalizedResource LOCALIZED_INVALID_UUID = new LocalizedResource("verify.arg.uuid.is.not.valid", "must be a valid uuid matching pattern {0} got '{1}'");
  private static final LocalizedResource LOCALIZED_INVALID_VALUE_PATTERN = new LocalizedResource("verify.arg.value.pattern.is.not.valid", "must be a valid pattern matching {0} got '{1}'");
  private static final LocalizedResource LOCALIZED_MUST_BE_EMPTY = new LocalizedResource("verify.arg.must.be.empty", "must be empty or null");
  private static final LocalizedResource LOCALIZED_MUST_BE_EQUAL_TO = new LocalizedResource("verify.arg.must.be.equal.to", "must be equals to {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_GREATER_THAN = new LocalizedResource("verify.arg.must.be.greater.than", "must be greater than {1}, got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_GREATER_THAN_EQUAL = new LocalizedResource("verify.arg.must.be.greater.than.equal", "must be greater than or equal to {1}, got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_IN_LENGTH_RANGE = new LocalizedResource("verify.arg.must.be.in.length.range", "must have a length between {0} and {1} got {2}");
  private static final LocalizedResource LOCALIZED_MUST_BE_IN_RANGE = new LocalizedResource("verify.arg.must.be.in.range", "must be in the range of {0} to {1}: {2}");
  private static final LocalizedResource LOCALIZED_MUST_BE_LESS_THAN = new LocalizedResource("verify.arg.must.be.less.than", "must be less than {1}, got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_LESS_THAN_EQUAL = new LocalizedResource("verify.arg.must.be.less.than.equal", "must be less than or equal to {1}, got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_NEGATIVE = new LocalizedResource("verify.arg.must.be.negative", "must be a negative number. got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_NOT_NEGATIVE = new LocalizedResource("verify.arg.must.be.not.negative", "must be a not negative number. got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_NOT_POSITIVE = new LocalizedResource("verify.arg.must.be.not.positive", "must be a not positive number. got {0}");
  private static final LocalizedResource LOCALIZED_MUST_BE_OF_LENGTH = new LocalizedResource("verify.arg.must.be.of.length", "must have a length of {0} got {1}");
  private static final LocalizedResource LOCALIZED_MUST_BE_POSITIVE = new LocalizedResource("verify.arg.must.be.positive", "must be a positive number. got {0}");
  private static final LocalizedResource LOCALIZED_MUST_NOT_BE_EMPTY = new LocalizedResource("verify.arg.must.not.be.empty", "must not be empty");
  private static final LocalizedResource LOCALIZED_MUST_NOT_BE_EQUAL_TO = new LocalizedResource("verify.arg.must.not.be.equal.to", "must not be equals to {0}");
  private static final LocalizedResource LOCALIZED_MUST_NOT_BE_NULL = new LocalizedResource("verify.arg.must.not.be.null", "must not be null");
  private static final LocalizedResource LOCALIZED_VALUE_IN_BLACK_LIST = new LocalizedResource("verify.arg.value.is.not.valid.in.list", "value {0} is not allowed");
  private static final LocalizedResource LOCALIZED_VALUE_IS_NOT_VALID = new LocalizedResource("verify.arg.value.is.not.valid", "is not valid");
  private static final LocalizedResource LOCALIZED_VALUE_MUST_BE_TYPE = new LocalizedResource("verify.arg.must.be.type", "must be a {0}");
  private static final LocalizedResource LOCALIZED_VALUE_NOT_IN_LIST = new LocalizedResource("verify.arg.value.is.not.valid.not.in.list", "value {0} is not acceptable. expected one of {1}");

  private Verify() {
    // no-op
  }

  public static void expect(final String name, final boolean isValid) {
    if (!isValid) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_IS_NOT_VALID);
    }
  }

  // ================================================================================
  //  Verify Not Null
  // ================================================================================
  public static <T> T expectNotNull(final String name, final T value) {
    if (value == null) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_NULL);
    }
    return value;
  }

  // ================================================================================
  //  Verify Empty
  // ================================================================================
  public static String expectEmpty(final String name, final String value) {
    if (StringUtil.isNotEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EMPTY);
    }
    return value;
  }

  // ================================================================================
  //  Verify Not Empty
  // ================================================================================
  public static String expectNotEmpty(final String name, final String value) {
    if (StringUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static byte[] expectNotEmpty(final String name, final byte[] value) {
    if (ArrayUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static int[] expectNotEmpty(final String name, final int[] value) {
    if (ArrayUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static long[] expectNotEmpty(final String name, final long[] value) {
    if (ArrayUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static <T> T[] expectNotEmpty(final String name, final T[] value) {
    if (ArrayUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static <T> Collection<T> expectNotEmpty(final String name, final Collection<T> value) {
    if (ListUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static <K, V> Map<K, V> expectNotEmpty(final String name, final Map<K, V> value) {
    if (MapUtil.isEmpty(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  public static <T> T expectNotEmpty(final String name, final T value) {
    if (value == null) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EMPTY);
    }
    return value;
  }

  // ================================================================================
  //  Verify Range
  // ================================================================================
  public static int expectInRange(final String name, final int value, final int minValue, final int maxValue) {
    if (value < minValue || value > maxValue) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_RANGE, minValue, maxValue, value);
    }
    return value;
  }

  public static long expectInRange(final String name, final long value, final long minValue, final long maxValue) {
    if (value < minValue || value > maxValue) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_RANGE, minValue, maxValue, value);
    }
    return value;
  }

  public static float expectInRange(final String name, final float value, final float minValue, final float maxValue) {
    if (value < minValue || value > maxValue) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_RANGE, minValue, maxValue, value);
    }
    return value;
  }

  public static double expectInRange(final String name, final double value, final double minValue, final double maxValue) {
    if (value < minValue || value > maxValue) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_RANGE, minValue, maxValue, value);
    }
    return value;
  }

  // ================================================================================
  //  Verify Equals
  // ================================================================================
  public static int expectEquals(final String name, final int value, final int expected) {
    if (value != expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static long expectEquals(final String name, final long value, final long expected) {
    if (value != expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static float expectEquals(final String name, final float value, final float expected, final float delta) {
    if (Math.abs(value - expected) > delta) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static double expectEquals(final String name, final double value, final double expected, final double delta) {
    if (Math.abs(value - expected) > delta) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static <T> T expectEquals(final String name, final T value, final T expected) {
    if (!Objects.equals(value, expected)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_EQUAL_TO, expected);
    }
    return value;
  }

  // ================================================================================
  //  Verify Not Equals
  // ================================================================================
  public static int expectNotEquals(final String name, final int value, final int expected) {
    if (value == expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static long expectNotEquals(final String name, final long value, final long expected) {
    if (value == expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static float expectNotEquals(final String name, final float value, final float expected, final float delta) {
    if (Math.abs(value - expected) < delta) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static double expectNotEquals(final String name, final double value, final double expected, final double delta) {
    if (Math.abs(value - expected) < delta) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EQUAL_TO, expected);
    }
    return value;
  }

  public static <T> T expectNotEquals(final String name, final T value, final T expected) {
    if (Objects.equals(value, expected)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_NOT_BE_EQUAL_TO, expected);
    }
    return value;
  }

  // ================================================================================
  //  Verify Not Zero
  // ================================================================================
  public static int expectNotZero(final String name, final int value) {
    return expectNotEquals(name, value, 0);
  }

  public static long expectNotZero(final String name, final long value) {
    return expectNotEquals(name, value, 0);
  }

  public static float expectNotZero(final String name, final float value, final float delta) {
    return expectNotEquals(name, value, 0.0f, delta);
  }

  public static double expectNotZero(final String name, final double value, final double delta) {
    return expectNotEquals(name, value, 0.0, delta);
  }

  // ================================================================================
  //  Verify Positive
  // ================================================================================
  public static int expectPositive(final String name, final int value) {
    if (value <= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_POSITIVE, value);
    }
    return value;
  }

  public static long expectPositive(final String name, final long value) {
    if (value <= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_POSITIVE, value);
    }
    return value;
  }

  public static float expectPositive(final String name, final float value) {
    if (value <= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_POSITIVE, value);
    }
    return value;
  }

  public static double expectPositive(final String name, final double value) {
    if (value <= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_POSITIVE, value);
    }
    return value;
  }

  // ================================================================================
  //  Verify Negative
  // ================================================================================
  public static int expectNegative(final String name, final int value) {
    if (value >= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_NEGATIVE, value);
    }
    return value;
  }

  public static long expectNegative(final String name, final long value) {
    if (value >= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_NEGATIVE, value);
    }
    return value;
  }

  public static float expectNegative(final String name, final float value) {
    if (value >= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_NEGATIVE, value);
    }
    return value;
  }

  public static double expectNegative(final String name, final double value) {
    if (value >= 0) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_NEGATIVE, value);
    }
    return value;
  }

  // ================================================================================
  //  Verify GreaterThan[Equal]
  // ================================================================================
  public static int expectGreaterThan(final String name, final int value, final int expected) {
    if (value <= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN, value, expected);
    }
    return value;
  }

  public static long expectGreaterThan(final String name, final long value, final long expected) {
    if (value <= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN, value, expected);
    }
    return value;
  }

  public static float expectGreaterThan(final String name, final float value, final float expected) {
    if (value <= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN, value, expected);
    }
    return value;
  }

  public static double expectGreaterThan(final String name, final double value, final double expected) {
    if (value <= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN, value, expected);
    }
    return value;
  }

  public static int expectGreaterThanEqual(final String name, final int value, final int expected) {
    if (value < expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static long expectGreaterThanEqual(final String name, final long value, final long expected) {
    if (value < expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static float expectGreaterThanEqual(final String name, final float value, final float expected) {
    if (value < expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static double expectGreaterThanEqual(final String name, final double value, final double expected) {
    if (value < expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_GREATER_THAN_EQUAL, value, expected);
    }
    return value;
  }

  // ================================================================================
  //  Verify LessThan[Equal]
  // ================================================================================
  public static int expectLessThan(final String name, final int value, final int expected) {
    if (value >= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN, value, expected);
    }
    return value;
  }

  public static long expectLessThan(final String name, final long value, final long expected) {
    if (value >= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN, value, expected);
    }
    return value;
  }

  public static float expectLessThan(final String name, final float value, final float expected) {
    if (value >= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN, value, expected);
    }
    return value;
  }

  public static double expectLessThan(final String name, final double value, final double expected) {
    if (value >= expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN, value, expected);
    }
    return value;
  }

  public static int expectLessThanEqual(final String name, final int value, final int expected) {
    if (value > expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static long expectLessThanEqual(final String name, final long value, final long expected) {
    if (value > expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static float expectLessThanEqual(final String name, final float value, final float expected) {
    if (value > expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN_EQUAL, value, expected);
    }
    return value;
  }

  public static double expectLessThanEqual(final String name, final double value, final double expected) {
    if (value > expected) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_LESS_THAN_EQUAL, value, expected);
    }
    return value;
  }

  // ================================================================================
  //  Verify Length
  // ================================================================================
  public static String expectLength(final String name, final String value, final int expectedLength) {
    final int length = StringUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static byte[] expectLength(final String name, final byte[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static int[] expectLength(final String name, final int[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static long[] expectLength(final String name, final long[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static float[] expectLength(final String name, final float[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static double[] expectLength(final String name, final double[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  public static <T> T[] expectLength(final String name, final T[] value, final int expectedLength) {
    final int length = ArrayUtil.length(value);
    if (length != expectedLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_OF_LENGTH, expectedLength, length);
    }
    return value;
  }

  // ================================================================================
  //  Verify Length Range
  // ================================================================================
  public static String expectLength(final String name, final String value, final int minLength, final int maxLength) {
    final int length = StringUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static byte[] expectLength(final String name, final byte[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static int[] expectLength(final String name, final int[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static long[] expectLength(final String name, final long[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static float[] expectLength(final String name, final float[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static double[] expectLength(final String name, final double[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  public static <T> T[] expectLength(final String name, final T[] value, final int minLength, final int maxLength) {
    final int length = ArrayUtil.length(value);
    if (length < minLength || length > maxLength) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_MUST_BE_IN_LENGTH_RANGE, minLength, maxLength, length);
    }
    return value;
  }

  // ================================================================================
  //  Verify In List
  // ================================================================================
  public static <T extends Enum<T>> T expectInList(final String name, final String value, final T[] values) {
    for (int i = 0, n = values.length; i < n; ++i) {
      if (StringUtil.equals(value, values[i].name())) {
        return values[i];
      }
    }
    throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_NOT_IN_LIST, value, Arrays.toString(values));
  }

  public static String expectInList(final String name, final String value, final String... items) {
    for (int i = 0, n = items.length; i < n; ++i) {
      if (StringUtil.equals(value, items[i])) {
        return value;
      }
    }
    throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_NOT_IN_LIST, value, Arrays.toString(items));
  }

  public static <T> T expectInList(final String name, final T value, final T[] list) {
    for (int i = 0, n = list.length; i < n; ++i) {
      if (Objects.equals(value, list[i])) {
        return value;
      }
    }
    throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_NOT_IN_LIST, value, Arrays.toString(list));
  }

  public static <T> T expectInList(final String name, final T value, final Set<T> list) {
    if (!list.contains(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_NOT_IN_LIST);
    }
    return value;
  }

  public static <T> T expectNotInList(final String name, final T value, final Set<T> list) {
    if (list.contains(value)) {
      throw new VerifyArgInvalidArgumentException(name, LOCALIZED_VALUE_IN_BLACK_LIST);
    }
    return value;
  }

  // ================================================================================
  //  Verify RegEx Helpers
  // ================================================================================
  public static String expectValidPattern(final String name, final Pattern pattern, final String value) {
    if (StringUtil.isNotEmpty(value) && pattern.matcher(value).matches()) return value;
    throw new VerifyArgInvalidArgumentException(name, LOCALIZED_INVALID_VALUE_PATTERN, pattern, value);
  }

  // Only checks that the format '8-4-4-4-12' is respected with allowed digits '0-9a-f'
  // Does not check version related constraints (uuid1, uuid4, ...)
  private static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
  private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX, Pattern.CASE_INSENSITIVE);
  public static String expectValidUuid(final String name, final String uuid) {
    if (StringUtil.isNotEmpty(uuid) && UUID_PATTERN.matcher(uuid).matches()) return uuid;
    throw new VerifyArgInvalidArgumentException(name, LOCALIZED_INVALID_UUID, UUID_REGEX, uuid);
  }

  // ================================================================================
  //  VerifyArgInvalidArgument Exception
  // ================================================================================
  public static class VerifyArgInvalidArgumentException extends IllegalArgumentException {
    @Serial private static final long serialVersionUID = -6683179816121065130L;

    private final String argName;

    public VerifyArgInvalidArgumentException() {
      super(LocalizedText.INSTANCE.get(LOCALIZED_INVALID_ARGS));
      this.argName = null;
    }

    public VerifyArgInvalidArgumentException(final String argName,
        final LocalizedResource resourceId, final Object... args) {
      super(argName + " " + LocalizedText.INSTANCE.get(resourceId, args));
      this.argName = argName;
    }

    public String getArgName() {
      return argName;
    }
  }

  public interface DataVerification {
    void verifyData() throws IllegalArgumentException;
  }
}
