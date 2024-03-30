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
import io.github.matteobertozzi.rednaco.hashes.CryptographicHash.HashAlgo;

public class TestCryptographicHash {
  @Test
  public void testHashReset() {
    final CryptographicHash hash = CryptographicHash.of(HashAlgo.SHA3_256);
    Assertions.assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", hash.hexDigest());
    Assertions.assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", hash.hexDigest());
    // digest() is resetting
    hash.update("hello");
    Assertions.assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", hash.hexDigest());
    // digest() is resetting
    hash.update("hello");
    Assertions.assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", hash.hexDigest());
  }

  @Test
  public void testHashUpdate() {
    final CryptographicHash hash = CryptographicHash.of(HashAlgo.SHA_256);
    hash.update((byte)0x10);
    hash.updateUtf8("hello");
    hash.update(new byte[] { 1, (byte)255, 37 });
    hash.update(IntEncoder.BIG_ENDIAN, 10);
    hash.update(IntEncoder.LITTLE_ENDIAN, 10);
    hash.update(UUID.fromString("09f27a17-4b3b-4127-82bd-30e0de520de5"));
    hash.update(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 2, 3);
    Assertions.assertEquals("67d10ffa300d7a74b5c481020a79fb516817dd0185ad9032e94be2ffdc624c24", hash.hexDigest());
  }
}
