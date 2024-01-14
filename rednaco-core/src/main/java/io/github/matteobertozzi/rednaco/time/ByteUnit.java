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

package io.github.matteobertozzi.rednaco.time;

import io.github.matteobertozzi.rednaco.strings.HumansUtil;

public class ByteUnit implements Comparable<ByteUnit> {
  private final long bytes;

  private ByteUnit(final long bytes) {
    this.bytes = bytes;
  }

  public long toBytes() { return bytes; }
  public long toKiloBytes() { return bytes >>> 10; }
  public long toMegaBytes() { return bytes >>> 20; }
  public long toGigaBytes() { return bytes >>> 30; }
  public long toTeraBytes() { return bytes >>> 40; }
  public long toPetaBytes() { return bytes >>> 50; }
  public long toExaBytes() { return bytes >>> 60; }

  public static ByteUnit ofBytes(final long bytes) {
    return new ByteUnit(bytes);
  }

  public static ByteUnit ofKiloBytes(final long kib) {
    return new ByteUnit(kib << 10);
  }

  public static ByteUnit ofMegaBytes(final long mib) {
    return new ByteUnit(mib << 20);
  }

  public static ByteUnit ofGigaBytes(final long gib) {
    return new ByteUnit(gib << 30);
  }

  public static ByteUnit ofTeraBytes(final long tib) {
    return new ByteUnit(tib << 40);
  }

  public static ByteUnit ofPetaBytes(final long pib) {
    return new ByteUnit(pib << 50);
  }

  public static ByteUnit ofExaBytes(final long eib) {
    return new ByteUnit(eib << 60);
  }

  @Override
  public int compareTo(final ByteUnit other) {
    return Long.compare(bytes, other.bytes);
  }

  @Override
  public int hashCode() {
    return Long.hashCode(bytes);
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof final ByteUnit other && bytes == other.bytes;
  }

  @Override
  public String toString() {
    return HumansUtil.humanBytes(bytes);
  }
}
