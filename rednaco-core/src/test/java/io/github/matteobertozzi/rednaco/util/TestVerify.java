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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import io.github.matteobertozzi.rednaco.util.Verify.VerifyArgInvalidArgumentException;

public class TestVerify {
  enum TestEnum { AAA, BBB, CCC };
  record Foo(int a, int b) {}

  @Test
  public void testExpect() {
    Verify.expect("test", true);
    assertInvalidArgument(() -> Verify.expect("test", false));
  }

  // ================================================================================
  //  Verify Not Null
  // ================================================================================
  @Test
  public void testExpectNotNull() {
    assertEquals("value", Verify.expectNotNull("test", "value"));
    assertInvalidArgument(() -> Verify.expectNotNull("test", null));
  }

  // ================================================================================
  //  Verify Empty
  // ================================================================================
  @Test
  public void testExpectEmpty() {
    assertEquals("", Verify.expectEmpty("test", ""));
    assertInvalidArgument(() -> Verify.expectEmpty("test", "value"));
  }

  // ================================================================================
  //  Verify Not Empty
  // ================================================================================
  @Test
  public void testExpectNotEmptyString() {
    assertEquals("value", Verify.expectNotEmpty("test", "value"));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", ""));
  }

  @Test
  public void testExpectNotEmptyByteArray() {
    final byte[] byteArray = { 1, 2, 3 };
    assertArrayEquals(byteArray, Verify.expectNotEmpty("test", byteArray));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (byte[])null));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", new byte[0]));
  }

  @Test
  public void testExpectNotEmptyIntArray() {
    final int[] intArray = { 1, 2, 3 };
    assertArrayEquals(intArray, Verify.expectNotEmpty("test", intArray));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (int[])null));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", new int[0]));
  }

  @Test
  public void testExpectNotEmptyLongArray() {
    final long[] longArray = { 1L, 2L, 3L };
    assertArrayEquals(longArray, Verify.expectNotEmpty("test", longArray));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (long[])null));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", new long[0]));
  }

  @Test
  public void testExpectNotEmptyCollection() {
    final List<String> values = List.of("aaa", "bbb", "ccc");
    assertEquals(values, Verify.expectNotEmpty("test", values));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (List<?>)null));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", List.of()));
  }

  @Test
  public void testExpectNotEmptyMap() {
    final Map<String, String> values = Map.of("key", "value");
    assertEquals(values, Verify.expectNotEmpty("test", values));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (Map<?,?>)null));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", Map.of()));
  }

  @Test
  public void testExpectNotEmptyObject() {
    final Foo obj = new Foo(1, 2);
    assertEquals(obj, Verify.expectNotEmpty("test", obj));
    assertInvalidArgument(() -> Verify.expectNotEmpty("test", (Foo)null));
  }

  // ================================================================================
  //  Verify Range
  // ================================================================================
  @Test
  public void testExpectInRangeInt() {
    assertEquals(5, Verify.expectInRange("test", 5, 1, 10));
    assertInvalidArgument(() -> Verify.expectInRange("test", 15, 1, 10));
  }

  @Test
  public void testExpectInRangeLong() {
    assertEquals(5L, Verify.expectInRange("test", 5L, 1L, 10L));
    assertInvalidArgument(() -> Verify.expectInRange("test", 15L, 1L, 10L));
  }

  @Test
  public void testExpectInRangeFloat() {
    assertEquals(0.5f, Verify.expectInRange("test-in-range", 0.5f, 0.1f, 0.9f), 0.00001f);
    assertInvalidArgument(() -> Verify.expectInRange("test-out-range", 0.0f, 0.1f, 0.9f));
  }

  @Test
  public void testExpectInRangeDouble() {
    assertEquals(0.5, Verify.expectInRange("test-in-range", 0.5, 0.1, 0.9), 0.00001);
    assertInvalidArgument(() -> Verify.expectInRange("test-out-range", 0.0, 0.1, 0.9));
  }

  // ================================================================================
  //  Verify Equals
  // ================================================================================
  @Test
  public void testExpectEqualsInt() {
    assertEquals(10L, Verify.expectEquals("test", 10, 10));
    assertInvalidArgument(() -> Verify.expectEquals("test", 10, 100));
  }

  @Test
  public void testExpectEqualsLong() {
    assertEquals(10L, Verify.expectEquals("test", 10L, 10L));
    assertInvalidArgument(() -> Verify.expectEquals("test", 10L, 100L));
  }

  @Test
  public void testExpectEqualsFloat() {
    assertEquals(0.5f, Verify.expectEquals("test", 0.5f, 0.5000000002f, 0.00001f), 0.00001f);
    assertInvalidArgument(() -> Verify.expectEquals("test", 0.0f, 0.5f, 0.0001f));
  }

  @Test
  public void testExpectEqualsDouble() {
    assertEquals(0.5, Verify.expectEquals("test", 0.5, 0.5000000002, 0.00001), 0.00001);
    assertInvalidArgument(() -> Verify.expectEquals("test", 0.0, 5.5, 0.0001));
  }

  public void testExpectEqualsObject() {
    final Foo value = new Foo(10, 20);
    assertEquals(value, Verify.expectEquals("test", value, new Foo(10, 20)));
    assertInvalidArgument(() -> Verify.expectEquals("test", value, new Foo(100, 200)));
  }

  // ================================================================================
  //  Verify Not Zero
  // ================================================================================
  @Test
  public void testExpectedNonZero() {
    Assertions.assertEquals(20, Verify.expectNotZero("test", 20));
    Assertions.assertEquals(-20, Verify.expectNotZero("test", -20));
    assertInvalidArgument(() -> Verify.expectNotZero("test", 0));

    Assertions.assertEquals(20L, Verify.expectNotZero("test", 20L));
    Assertions.assertEquals(-20L, Verify.expectNotZero("test", -20L));
    assertInvalidArgument(() -> Verify.expectNotZero("test", 0L));

    Assertions.assertEquals(20.5f, Verify.expectNotZero("test", 20.5f, 0.00001f), 0.00001);
    Assertions.assertEquals(-20.5f, Verify.expectNotZero("test", -20.5f, 0.00001f), 0.00001);
    assertInvalidArgument(() -> Verify.expectNotZero("test", 0.0f, 0.00001f));

    Assertions.assertEquals(20.5, Verify.expectNotZero("test", 20.5, 0.00001), 0.00001);
    Assertions.assertEquals(-20.5, Verify.expectNotZero("test", -20.5, 0.00001), 0.00001);
    assertInvalidArgument(() -> Verify.expectNotZero("test", 0.0, 0.00001));
  }

  // ================================================================================
  //  Verify Positive
  // ================================================================================
  @Test
  public void testExpectedPositive() {
    Assertions.assertEquals(20, Verify.expectPositive("test", 20));
    assertInvalidArgument(() -> Verify.expectPositive("test", 0));
    assertInvalidArgument(() -> Verify.expectPositive("test", -10));

    Assertions.assertEquals(20L, Verify.expectPositive("test", 20L));
    assertInvalidArgument(() -> Verify.expectPositive("test", 0L));
    assertInvalidArgument(() -> Verify.expectPositive("test", -10L));

    Assertions.assertEquals(20.5f, Verify.expectPositive("test", 20.5f));
    assertInvalidArgument(() -> Verify.expectPositive("test", 0f));
    assertInvalidArgument(() -> Verify.expectPositive("test", -10.5f));

    Assertions.assertEquals(20.5, Verify.expectPositive("test", 20.5));
    assertInvalidArgument(() -> Verify.expectPositive("test", 0));
    assertInvalidArgument(() -> Verify.expectPositive("test", -10.5));
  }

  // ================================================================================
  //  Verify Negative
  // ================================================================================
  @Test
  public void testExpectedNegative() {
    Assertions.assertEquals(-20, Verify.expectNegative("test", -20));
    assertInvalidArgument(() -> Verify.expectNegative("test", 0));
    assertInvalidArgument(() -> Verify.expectNegative("test", 10));

    Assertions.assertEquals(-20L, Verify.expectNegative("test", -20L));
    assertInvalidArgument(() -> Verify.expectNegative("test", 0L));
    assertInvalidArgument(() -> Verify.expectNegative("test", 10L));

    Assertions.assertEquals(-20.5f, Verify.expectNegative("test", -20.5f));
    assertInvalidArgument(() -> Verify.expectNegative("test", 0f));
    assertInvalidArgument(() -> Verify.expectNegative("test", 10.5f));

    Assertions.assertEquals(-20.5, Verify.expectNegative("test", -20.5));
    assertInvalidArgument(() -> Verify.expectNegative("test", 0));
    assertInvalidArgument(() -> Verify.expectNegative("test", 10.5));
  }

  // ================================================================================
  //  Verify GreaterThan[Equal]
  // ================================================================================
  @Test
  public void testExpectedGreater() {
    Assertions.assertEquals(10, Verify.expectGreaterThan("test", 10, 5));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10, 10));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10, 50));

    Assertions.assertEquals(10L, Verify.expectGreaterThan("test", 10L, 5L));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10L, 10L));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10L, 50L));

    Assertions.assertEquals(10.5f, Verify.expectGreaterThan("test", 10.5f, 5.5f), 0.00001);
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10.5f, 10.5f));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10.5f, 50.5f));

    Assertions.assertEquals(10.5, Verify.expectGreaterThan("test", 10.5, 5.5), 0.00001);
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10.5, 10.5));
    assertInvalidArgument(() -> Verify.expectGreaterThan("test", 10.5, 50.5));
  }

  @Test
  public void testExpectedGreaterThanEqual() {
    Assertions.assertEquals(10, Verify.expectGreaterThanEqual("test", 10, 10));
    Assertions.assertEquals(10, Verify.expectGreaterThanEqual("test", 10, 5));
    assertInvalidArgument(() -> Verify.expectGreaterThanEqual("test", 10, 50));

    Assertions.assertEquals(10L, Verify.expectGreaterThanEqual("test", 10L, 10L));
    Assertions.assertEquals(10L, Verify.expectGreaterThanEqual("test", 10L, 5L));
    assertInvalidArgument(() -> Verify.expectGreaterThanEqual("test", 10L, 50L));

    Assertions.assertEquals(10.5f, Verify.expectGreaterThanEqual("test", 10.5f, 10.5f), 0.00001);
    Assertions.assertEquals(10.5f, Verify.expectGreaterThanEqual("test", 10.5f, 5.5f), 0.00001);
    assertInvalidArgument(() -> Verify.expectGreaterThanEqual("test", 10.5f, 50.5f));

    Assertions.assertEquals(10.5, Verify.expectGreaterThanEqual("test", 10.5, 10.5), 0.00001);
    Assertions.assertEquals(10.5, Verify.expectGreaterThanEqual("test", 10.5, 5.5), 0.00001);
    assertInvalidArgument(() -> Verify.expectGreaterThanEqual("test", 10.5, 50.5));
  }

  // ================================================================================
  //  Verify LessThan[Equal]
  // ================================================================================
  @Test
  public void testExpectedLess() {
    Assertions.assertEquals(10, Verify.expectLessThan("test", 10, 20));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10, 10));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10, 5));

    Assertions.assertEquals(10L, Verify.expectLessThan("test", 10L, 20L));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10L, 10L));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10L, 5L));

    Assertions.assertEquals(10.5f, Verify.expectLessThan("test", 10.5f, 20.5f), 0.00001);
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10.5f, 10.5f));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10.5f, 5.5f));

    Assertions.assertEquals(10.5, Verify.expectLessThan("test", 10.5, 20.5), 0.00001);
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10.5, 10.5));
    assertInvalidArgument(() -> Verify.expectLessThan("test", 10.5, 5.5));
  }

  @Test
  public void testExpectedLessThanEqual() {
    Assertions.assertEquals(10, Verify.expectLessThanEqual("test", 10, 10));
    Assertions.assertEquals(10, Verify.expectLessThanEqual("test", 10, 20));
    assertInvalidArgument(() -> Verify.expectLessThanEqual("test", 10, 5));

    Assertions.assertEquals(10L, Verify.expectLessThanEqual("test", 10L, 10L));
    Assertions.assertEquals(10L, Verify.expectLessThanEqual("test", 10L, 20L));
    assertInvalidArgument(() -> Verify.expectLessThanEqual("test", 10L, 5L));

    Assertions.assertEquals(10.5f, Verify.expectLessThanEqual("test", 10.5f, 10.5f), 0.00001);
    Assertions.assertEquals(10.5f, Verify.expectLessThanEqual("test", 10.5f, 20.5f), 0.00001);
    assertInvalidArgument(() -> Verify.expectLessThanEqual("test", 10.5f, 5f));

    Assertions.assertEquals(10.5, Verify.expectLessThanEqual("test", 10.5, 10.5), 0.00001);
    Assertions.assertEquals(10.5, Verify.expectLessThanEqual("test", 10.5, 20.5), 0.00001);
    assertInvalidArgument(() -> Verify.expectLessThanEqual("test", 10.5, 5.5));
  }

  // ================================================================================
  //  Verify Length
  // ================================================================================
  @Test
  public void testExpectedLength() {
    final String sValue = "hello";
    Assertions.assertEquals(sValue, Verify.expectLength("test", sValue, 5));
    assertInvalidArgument(() -> Verify.expectLength("test", sValue, 10));

    final int[] iArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(iArray, Verify.expectLength("test", iArray, 6));
    assertInvalidArgument(() -> Verify.expectLength("test", iArray, 10));

    final long[] lArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(lArray, Verify.expectLength("test", lArray, 6));
    assertInvalidArgument(() -> Verify.expectLength("test", lArray, 10));

    final float[] fArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(fArray, Verify.expectLength("test", fArray, 6));
    assertInvalidArgument(() -> Verify.expectLength("test", fArray, 10));

    final double[] dArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(dArray, Verify.expectLength("test", dArray, 6));
    assertInvalidArgument(() -> Verify.expectLength("test", dArray, 10));

    final Foo[] oArray = { new Foo(1, 2), new Foo(3, 4), new Foo(5, 6), new Foo(7, 8), new Foo(9, 10) };
    Assertions.assertArrayEquals(oArray, Verify.expectLength("test", oArray, 5));
    assertInvalidArgument(() -> Verify.expectLength("test", oArray, 10));
  }

  // ================================================================================
  //  Verify Length Range
  // ================================================================================
  @Test
  public void testExpectedLengthRange() {
    final String sValue = "hello";
    Assertions.assertEquals(sValue, Verify.expectLength("test", sValue, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", sValue, 2, 4));

    final int[] iArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(iArray, Verify.expectLength("test", iArray, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", iArray, 2, 4));

    final long[] lArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(lArray, Verify.expectLength("test", lArray, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", lArray, 2, 4));

    final float[] fArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(fArray, Verify.expectLength("test", fArray, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", fArray, 2, 4));

    final double[] dArray = { 1, 2, 3, 4, 5, 6 };
    Assertions.assertArrayEquals(dArray, Verify.expectLength("test", dArray, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", dArray, 2, 4));

    final Foo[] oArray = { new Foo(1, 2), new Foo(3, 4), new Foo(5, 6), new Foo(7, 8), new Foo(9, 10) };
    Assertions.assertArrayEquals(oArray, Verify.expectLength("test", oArray, 3, 7));
    assertInvalidArgument(() -> Verify.expectLength("test", oArray, 2, 4));
  }

  // ================================================================================
  //  Verify In List
  // ================================================================================
  @Test
  public void expecInList() {
    Assertions.assertEquals(TestEnum.AAA, Verify.expectInList("test", "AAA", TestEnum.values()));
    assertInvalidArgument(() -> Verify.expectInList("test", "aaa", TestEnum.values()));

    Assertions.assertEquals("aaa", Verify.expectInList("test", "aaa", "aaa", "bbb"));
    assertInvalidArgument(() -> Verify.expectInList("test", "aaa", "bbb", "ccc"));

    Assertions.assertEquals("aaa", Verify.expectInList("test", "aaa", Set.of("aaa", "bbb")));
    assertInvalidArgument(() -> Verify.expectInList("test", "aaa", Set.of("bbb", "ccc")));
  }

  @Test
  public void expectNotInList() {
    Assertions.assertEquals("aaa", Verify.expectNotInList("test", "aaa", Set.of("bbb", "ccc")));
    assertInvalidArgument(() -> Verify.expectNotInList("test", "aaa", Set.of("aaa", "bbb")));
  }

  // ================================================================================
  //  Verify RegEx Helpers
  // ================================================================================
  @Test
  public void testValidUuid() {
    Verify.expectValidUuid("uuid_v1_0", "e0fd32da-6c56-11eb-b1ae-03c43ed0ca77");
    Verify.expectValidUuid("uuid_v1_1", "e0fd32db-6c56-11eb-b1ae-03c43ed0ca73");
    Verify.expectValidUuid("uuid_v1_2", "6027fba6-6c57-11eb-b1ae-03c43ed0ca73");
    Verify.expectValidUuid("uuid_v1_3", "4fc4915e-6c57-11eb-b1ae-03c43ed0ca73");
    Verify.expectValidUuid("uuid_v1_4", "682566d7-6c57-11eb-b1ae-03c43ed0ca73");

    Verify.expectValidUuid("uuid_v4_0", "435b0a68-e492-40c7-9e93-6a8611f7d1c6");
    Verify.expectValidUuid("uuid_v4_1", "c93ddbb0-ea00-4b4d-b608-1999d84c0edf");
    Verify.expectValidUuid("uuid_v4_2", "73ef035e-2c45-4817-b913-344cfd36ec4d");
    Verify.expectValidUuid("uuid_v4_3", "3cd25291-e2e1-41e0-b8d3-934d17445f7e");
    Verify.expectValidUuid("uuid_v4_4", "cafa01df-77a3-4afe-a6fe-3302c85ca90a");

    Verify.expectValidUuid("custom_uuid_0", "435b0a68-e492-40c7-9e93-6a8611f7d1c6");
    Verify.expectValidUuid("custom_uuid_1", "c93ddbb0-ea00-4b4d-b608-1999d84c0edf");
    Verify.expectValidUuid("custom_uuid_2", "73ef035e-2c45-4817-b913-344cfd36ec4d");
    Verify.expectValidUuid("custom_uuid_3", "3cd25291-e2e1-41e0-b8d3-934d17445f7e");
    Verify.expectValidUuid("custom_uuid_4", "cafa01df-77a3-4afe-a6fe-3302c85ca90a");

    assertInvalidArgument(() -> Verify.expectValidUuid("uuid_err_1", "435b0a68-e492-40c7-9e93-6a8611f7d1c"));
    assertInvalidArgument(() -> Verify.expectValidUuid("uuid_err_2", "1435b0a68-e492-40c7-9e93-6a8611f7d1c6"));
    assertInvalidArgument(() -> Verify.expectValidUuid("uuid_err_3", "1435b0a68e49240c79e936a8611f7d1c6"));
    assertInvalidArgument(() -> Verify.expectValidUuid("uuid_err_2", "435b0a68 e492 40c7 9e93 6a8611f7d1c6"));
    assertInvalidArgument(() -> Verify.expectValidUuid("uuid_err_2", "435b0a68-X492-40c7-9e93-6a8611f7d1c6"));
  }

  // ================================================================================
  //  Helpers
  // ================================================================================
  private void assertInvalidArgument(final Executable executable) {
    Assertions.assertThrows(VerifyArgInvalidArgumentException.class, executable);
  }
}
