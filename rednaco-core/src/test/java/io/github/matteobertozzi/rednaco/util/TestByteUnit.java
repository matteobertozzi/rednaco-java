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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.time.ByteUnit;

public class TestByteUnit {
  @Test
  public void testBytes() {
    Assertions.assertEquals(10, ByteUnit.ofBytes(10).toBytes());
    Assertions.assertEquals(6, ByteUnit.ofBytes(6L << 10).toKiloBytes());
    Assertions.assertEquals(5, ByteUnit.ofBytes(5L << 20).toMegaBytes());
    Assertions.assertEquals(4, ByteUnit.ofBytes(4L << 30).toGigaBytes());
    Assertions.assertEquals(3, ByteUnit.ofBytes(3L << 40).toTeraBytes());
    Assertions.assertEquals(2, ByteUnit.ofBytes(2L << 50).toPetaBytes());
    Assertions.assertEquals(1, ByteUnit.ofBytes(1L << 60).toExaBytes());
  }

  @Test
  public void testKiloBytes() {
    Assertions.assertEquals(2L << 10, ByteUnit.ofKiloBytes(2).toBytes());
    Assertions.assertEquals(3L, ByteUnit.ofKiloBytes(3).toKiloBytes());
    Assertions.assertEquals(6, ByteUnit.ofKiloBytes(6L << 10).toMegaBytes());
    Assertions.assertEquals(5, ByteUnit.ofKiloBytes(5L << 20).toGigaBytes());
    Assertions.assertEquals(4, ByteUnit.ofKiloBytes(4L << 30).toTeraBytes());
    Assertions.assertEquals(3, ByteUnit.ofKiloBytes(3L << 40).toPetaBytes());
    Assertions.assertEquals(2, ByteUnit.ofKiloBytes(2L << 50).toExaBytes());
  }

  @Test
  public void testMegaBytes() {
    Assertions.assertEquals(0, ByteUnit.ofMegaBytes(2).toGigaBytes());
    Assertions.assertEquals(2L << 20, ByteUnit.ofMegaBytes(2).toBytes());
    Assertions.assertEquals(3072, ByteUnit.ofMegaBytes(3).toKiloBytes());
    Assertions.assertEquals(7, ByteUnit.ofMegaBytes(7).toMegaBytes());
    Assertions.assertEquals(6, ByteUnit.ofMegaBytes(6L << 10).toGigaBytes());
    Assertions.assertEquals(5, ByteUnit.ofMegaBytes(5L << 20).toTeraBytes());
    Assertions.assertEquals(4, ByteUnit.ofMegaBytes(4L << 30).toPetaBytes());
    Assertions.assertEquals(3, ByteUnit.ofMegaBytes(3L << 40).toExaBytes());
  }

  @Test
  public void testGigaBytes() {
    Assertions.assertEquals(2L << 30, ByteUnit.ofGigaBytes(2).toBytes());
    Assertions.assertEquals(3L << 20, ByteUnit.ofGigaBytes(3).toKiloBytes());
    Assertions.assertEquals(7L << 10, ByteUnit.ofGigaBytes(7).toMegaBytes());
    Assertions.assertEquals(6, ByteUnit.ofGigaBytes(6).toGigaBytes());
    Assertions.assertEquals(5, ByteUnit.ofGigaBytes(5L << 10).toTeraBytes());
    Assertions.assertEquals(4, ByteUnit.ofGigaBytes(4L << 20).toPetaBytes());
    Assertions.assertEquals(3, ByteUnit.ofGigaBytes(3L << 30).toExaBytes());
  }

  @Test
  public void testTeraBytes() {
    Assertions.assertEquals(2L << 40, ByteUnit.ofTeraBytes(2).toBytes());
    Assertions.assertEquals(3L << 30, ByteUnit.ofTeraBytes(3).toKiloBytes());
    Assertions.assertEquals(7L << 20, ByteUnit.ofTeraBytes(7).toMegaBytes());
    Assertions.assertEquals(8L << 10, ByteUnit.ofTeraBytes(8).toGigaBytes());
    Assertions.assertEquals(5, ByteUnit.ofTeraBytes(5).toTeraBytes());
    Assertions.assertEquals(4, ByteUnit.ofTeraBytes(4L << 10).toPetaBytes());
    Assertions.assertEquals(3, ByteUnit.ofTeraBytes(3L << 20).toExaBytes());
  }

  @Test
  public void testPetaBytes() {
    Assertions.assertEquals(2L << 50, ByteUnit.ofPetaBytes(2).toBytes());
    Assertions.assertEquals(3L << 40, ByteUnit.ofPetaBytes(3).toKiloBytes());
    Assertions.assertEquals(7L << 30, ByteUnit.ofPetaBytes(7).toMegaBytes());
    Assertions.assertEquals(8L << 20, ByteUnit.ofPetaBytes(8).toGigaBytes());
    Assertions.assertEquals(9L << 10, ByteUnit.ofPetaBytes(9).toTeraBytes());
    Assertions.assertEquals(5, ByteUnit.ofPetaBytes(5).toPetaBytes());
    Assertions.assertEquals(3, ByteUnit.ofPetaBytes(3L << 10).toExaBytes());
  }


  @Test
  public void testExaBytes() {
    Assertions.assertEquals(2L << 60, ByteUnit.ofExaBytes(2).toBytes());
    Assertions.assertEquals(3L << 50, ByteUnit.ofExaBytes(3).toKiloBytes());
    Assertions.assertEquals(7L << 40, ByteUnit.ofExaBytes(7).toMegaBytes());
    Assertions.assertEquals(8L << 30, ByteUnit.ofExaBytes(8).toGigaBytes());
    Assertions.assertEquals(9L << 20, ByteUnit.ofExaBytes(9).toTeraBytes());
    Assertions.assertEquals(10L << 10, ByteUnit.ofExaBytes(10).toPetaBytes());
    Assertions.assertEquals(5, ByteUnit.ofExaBytes(5).toExaBytes());
  }
}
