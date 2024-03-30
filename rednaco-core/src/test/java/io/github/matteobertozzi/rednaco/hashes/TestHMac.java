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

package io.github.matteobertozzi.rednaco.hashes;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.matteobertozzi.rednaco.bytes.encoding.IntEncoder;
import io.github.matteobertozzi.rednaco.hashes.HMac.HMacAlgo;

public class TestHMac {
@Test
  public void testHashReset() {
    final HMac hash = HMac.of(HMacAlgo.SHA3_256, "hello world key".getBytes());
    Assertions.assertEquals("8988dd2d5adb7d73813534c8aa531c6e5723856c0e13b0292fef7e753546bedc", hash.hexDigest());
    Assertions.assertEquals("8988dd2d5adb7d73813534c8aa531c6e5723856c0e13b0292fef7e753546bedc", hash.hexDigest());
    // digest() is resetting
    hash.update("hello");
    Assertions.assertEquals("779fde62d5cfe9b049e30c808510a00914ceef8fe12f050150c3f9d09ed2c6aa", hash.hexDigest());
    // digest() is resetting
    hash.update("hello");
    Assertions.assertEquals("779fde62d5cfe9b049e30c808510a00914ceef8fe12f050150c3f9d09ed2c6aa", hash.hexDigest());
  }

  @Test
  public void testHashUpdate() {
    final HMac hash = HMac.of(HMacAlgo.SHA_256, "hello world key".getBytes());
    hash.update((byte)0x10);
    hash.updateUtf8("hello");
    hash.update(new byte[] { 1, (byte)255, 37 });
    hash.update(IntEncoder.BIG_ENDIAN, 10);
    hash.update(IntEncoder.LITTLE_ENDIAN, 10);
    hash.update(UUID.fromString("09f27a17-4b3b-4127-82bd-30e0de520de5"));
    hash.update(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 2, 3);
    Assertions.assertEquals("0526b9b712677829ee2495a9e2e02d35f387698d49002d565fbe28968b50f156", hash.hexDigest());
  }
}
