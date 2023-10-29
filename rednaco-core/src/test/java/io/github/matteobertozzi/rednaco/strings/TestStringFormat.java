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

public class TestStringFormat {
  @Test
  public void testNamedFormat() {
    Assertions.assertEquals("", StringFormat.namedFormat(""));
    Assertions.assertEquals("test", StringFormat.namedFormat("test"));
    Assertions.assertEquals("test 123", StringFormat.namedFormat("test {}", 123));
    Assertions.assertEquals("123 test", StringFormat.namedFormat("{} test", 123));
    Assertions.assertEquals("123 test {UNPROVIDED_ARG}", StringFormat.namedFormat("{} test {}", 123));

    Assertions.assertEquals("123 test foo test2", StringFormat.namedFormat("{} test {} test2", 123, "foo"));
    Assertions.assertEquals("123 test abc:foo test2", StringFormat.namedFormat("{} test {abc} test2", 123, "foo"));

    Assertions.assertEquals("[1, 2, 3] [true, false]", StringFormat.namedFormat("{} {}", new int[] { 1, 2, 3 }, new boolean[] { true, false }));
  }

  @Test
  public void testPositionalFormat() {
    Assertions.assertEquals("", StringFormat.positionalFormat(""));
    Assertions.assertEquals("test", StringFormat.positionalFormat("test"));
    Assertions.assertEquals("test {} test2", StringFormat.positionalFormat("test {} test2", 123));
    Assertions.assertEquals("test {foo} test2", StringFormat.positionalFormat("test {foo} test2", 123));
    Assertions.assertEquals("test 123", StringFormat.positionalFormat("test {0}", 123));
    Assertions.assertEquals("123 test", StringFormat.positionalFormat("{0} test", 123));
    Assertions.assertEquals("{UNPROVIDED_ARG} test", StringFormat.positionalFormat("{1} test", 123));

    Assertions.assertEquals("foo test 123 test2", StringFormat.positionalFormat("{1} test {0} test2", 123, "foo"));
    Assertions.assertEquals("foo test 123 test2", StringFormat.positionalFormat("{1} test {0:comment} test2", 123, "foo"));

    Assertions.assertEquals("123456789876", StringFormat.positionalFormat("{0}{1}{2:aaa}{3:bbb}", 123, 456, 789, 876));
    Assertions.assertEquals("[1, 2, 3] [true, false]", StringFormat.positionalFormat("{0} {1}", new int[] { 1, 2, 3 }, new boolean[] { true, false }));
  }

  @Test
  public void testValueOf() {
    Assertions.assertEquals("null", StringFormat.valueOf(null));
    Assertions.assertEquals("[]", StringFormat.valueOf(new int[0]));
    Assertions.assertEquals("[false, true, false]", StringFormat.valueOf(new boolean[] { false, true, false }));
    Assertions.assertEquals("[1, 2, 3]", StringFormat.valueOf(new int[] { 1, 2, 3 }));
    Assertions.assertEquals("[1, 2, 3]", StringFormat.valueOf(new short[] { 1, 2, 3 }));
    Assertions.assertEquals("[1, 2, 3]", StringFormat.valueOf(new long[] { 1, 2, 3 }));
    Assertions.assertEquals("[1.1, 2.2, 3.3]", StringFormat.valueOf(new float[] { 1.1f, 2.2f, 3.3f }));
    Assertions.assertEquals("[1.1, 2.2, 3.3]", StringFormat.valueOf(new double[] { 1.1, 2.2, 3.3 }));
    Assertions.assertEquals("[aaa, bbb, ccc]", StringFormat.valueOf(new String[] { "aaa", "bbb", "ccc" }));
    Assertions.assertEquals("{a:10}", StringFormat.valueOf(Map.of("a", 10)));
  }
}
