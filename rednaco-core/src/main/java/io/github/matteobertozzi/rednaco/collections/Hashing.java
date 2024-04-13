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
package io.github.matteobertozzi.rednaco.collections;

public final class Hashing {
  private static final long GOLDEN_RATIO_64 = 0x9e3779b97f4a7c15L;
  private static final int GOLDEN_RATIO_32 = 0x9e3779b9;

  private Hashing() {
    // no-op
  }

  public static int hash32(final Object key) {
    return keyHashCode(key.hashCode());
  }

  public static long keyHashCode(final long key) {
    final long x = key * GOLDEN_RATIO_64;
    return x ^ (x >>> 57);
  }

  public static int keyHashCode(final int key) {
    final int h = key * GOLDEN_RATIO_32;
    return h ^ (h >>> 24);
  }

  private static final int MAXIMUM_CAPACITY = 1 << 30;
  public static int tableSizeFor(final int capacity) {
    final int n = -1 >>> Integer.numberOfLeadingZeros(capacity - 1);
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
  }

  @FunctionalInterface
  public interface HashFunction64 {
    long hash(long h);
  }

  @FunctionalInterface
  public interface HashFunction32 {
    int hash(int h);
  }
}
