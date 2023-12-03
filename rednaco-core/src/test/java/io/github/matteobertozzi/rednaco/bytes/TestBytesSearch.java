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

package io.github.matteobertozzi.rednaco.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBytesSearch {
  @Test
  public void testHasPrefix() {
    Assertions.assertTrue(BytesSearch.hasPrefix(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 }));
    Assertions.assertTrue(BytesSearch.hasPrefix(new byte[] { 1, 2, 3, 4 }, new byte[] { 1, 2, 3 }));
    Assertions.assertFalse(BytesSearch.hasPrefix(new byte[] { 1, 2, 4 }, new byte[] { 1, 2, 3 }));
    Assertions.assertFalse(BytesSearch.hasPrefix(new byte[] { 6, 5, 4 }, new byte[] { 1, 2, 3 }));
  }

  @Test
  public void testPrefix() {
    final byte[] full = "hello world".getBytes();
    final byte[] prefixA = "hello worldo".getBytes();
    final byte[] prefixB = "hello".getBytes();
    final byte[] prefixC = "hello boom".getBytes();
    final byte[] prefixD = "booom".getBytes();

    Assertions.assertEquals(11, BytesSearch.prefix(full, full));
    Assertions.assertEquals(11, BytesSearch.prefix(full, prefixA));
    Assertions.assertEquals(5, BytesSearch.prefix(full, prefixB));
    Assertions.assertEquals(6, BytesSearch.prefix(full, prefixC));
    Assertions.assertEquals(0, BytesSearch.prefix(full, prefixD));
  }

  @Test
  public void testSuffix() {
    final byte[] full = "hello world".getBytes();
    final byte[] prefixA = "hhello world".getBytes();
    final byte[] prefixB = "world".getBytes();
    final byte[] prefixC = "boom world".getBytes();
    final byte[] prefixD = "boom".getBytes();

    Assertions.assertEquals(11, BytesSearch.suffix(full, full));
    Assertions.assertEquals(11, BytesSearch.suffix(full, prefixA));
    Assertions.assertEquals(5, BytesSearch.suffix(full, prefixB));
    Assertions.assertEquals(6, BytesSearch.suffix(full, prefixC));
    Assertions.assertEquals(0, BytesSearch.suffix(full, prefixD));
  }
}
