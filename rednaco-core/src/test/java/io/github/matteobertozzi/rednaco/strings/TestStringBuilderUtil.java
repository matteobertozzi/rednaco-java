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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStringBuilderUtil {
  @Test
  public void testAppendValue() {
    final StringBuilder builder = new StringBuilder();

    StringBuilderUtil.appendValue(builder, null);
    Assertions.assertEquals("null", builder.toString());

    StringBuilderUtil.appendValue(builder, 123);
    Assertions.assertEquals("null123", builder.toString());

    StringBuilderUtil.appendValue(builder, new byte[] { 1, 2 });
    Assertions.assertEquals("null123[1, 2]", builder.toString());

    StringBuilderUtil.appendValue(builder, new short[] { 1, 2 });
    Assertions.assertEquals("null123[1, 2][1, 2]", builder.toString());

    StringBuilderUtil.appendValue(builder, new int[] { 1, 2 });
    Assertions.assertEquals("null123[1, 2][1, 2][1, 2]", builder.toString());

    StringBuilderUtil.appendValue(builder, new String[] { "aaa", "bbb" });
    Assertions.assertEquals("null123[1, 2][1, 2][1, 2][aaa, bbb]", builder.toString());

    StringBuilderUtil.appendValue(builder, Map.of("a", 10));
    Assertions.assertEquals("null123[1, 2][1, 2][1, 2][aaa, bbb]{a:10}", builder.toString());
  }

  @Test
  public void testAppendArrayByte() {
    final StringBuilder builder = new StringBuilder();

    StringBuilderUtil.appendArray(builder, (byte[])null);
    Assertions.assertEquals("null", builder.toString());

    StringBuilderUtil.appendArray(builder, new byte[0]);
    Assertions.assertEquals("null[]", builder.toString());

    StringBuilderUtil.appendArray(builder, new byte[] { 1 });
    Assertions.assertEquals("null[][1]", builder.toString());

    StringBuilderUtil.appendArray(builder, new byte[] { 1, 2 });
    Assertions.assertEquals("null[][1][1, 2]", builder.toString());

    StringBuilderUtil.appendArray(builder, new byte[] { 1, 2, 3 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3]", builder.toString());

    StringBuilderUtil.appendArray(builder, new byte[] { 1, 2, 3, 4 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3][2, 3]", builder.toString());
  }

  @Test
  public void testAppendArrayInt() {
    final StringBuilder builder = new StringBuilder();

    StringBuilderUtil.appendArray(builder, (int[])null);
    Assertions.assertEquals("null", builder.toString());

    StringBuilderUtil.appendArray(builder, new int[0]);
    Assertions.assertEquals("null[]", builder.toString());

    StringBuilderUtil.appendArray(builder, new int[] { 1 });
    Assertions.assertEquals("null[][1]", builder.toString());

    StringBuilderUtil.appendArray(builder, new int[] { 1, 2 });
    Assertions.assertEquals("null[][1][1, 2]", builder.toString());

    StringBuilderUtil.appendArray(builder, new int[] { 1, 2, 3 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3]", builder.toString());

    StringBuilderUtil.appendArray(builder, new int[] { 1, 2, 3, 4 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3][2, 3]", builder.toString());
  }

  @Test
  public void testAppendArrayLong() {
    final StringBuilder builder = new StringBuilder();

    StringBuilderUtil.appendArray(builder, (long[])null);
    Assertions.assertEquals("null", builder.toString());

    StringBuilderUtil.appendArray(builder, new long[0]);
    Assertions.assertEquals("null[]", builder.toString());

    StringBuilderUtil.appendArray(builder, new long[] { 1 });
    Assertions.assertEquals("null[][1]", builder.toString());

    StringBuilderUtil.appendArray(builder, new long[] { 1, 2 });
    Assertions.assertEquals("null[][1][1, 2]", builder.toString());

    StringBuilderUtil.appendArray(builder, new long[] { 1, 2, 3 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3]", builder.toString());

    StringBuilderUtil.appendArray(builder, new long[] { 1, 2, 3, 4 }, 1, 2);
    Assertions.assertEquals("null[][1][1, 2][2, 3][2, 3]", builder.toString());
  }
}
